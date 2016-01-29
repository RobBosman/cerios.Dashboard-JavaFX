package nl.valori.dashboard.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.valori.dashboard.dataloader.DataLoader;
import nl.valori.dashboard.dataloader.XmlUtil;
import nl.valori.dashboard.loader.excel.ExcelObtainerDate;
import nl.valori.dashboard.loader.excel.ExcelObtainerName;
import nl.valori.dashboard.loader.excel.ExcelObtainerPeriod;
import nl.valori.dashboard.loader.excel.ExcelObtainerValue;
import nl.valori.dashboard.loader.excel.ExcelSource;
import nl.valori.dashboard.model.ImportHistory;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.KpiVariable;
import nl.valori.dashboard.model.Period;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ImportLoader extends XmlUtil {

    private static final String NAMESPACE = "http://www.valori.nl/dashboard/import";
    private static final Logger LOGGER = Logger.getLogger(ImportLoader.class);

    /**
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
	if (args.length != 1) {
	    LOGGER.error("Usage: " + ImportLoader.class.getSimpleName() + " <importConfig.xml>");
	    System.exit(1);
	}
	new ImportLoader().load(args[0]);
    }

    private LoaderDAO dao;
    private List<ImportDefinition> importDefinitions;
    private List<ExcelSource> importExcelSources;
    private Map<String, KpiHolder> kpiHolderMap;

    public ImportLoader() {
	importDefinitions = new ArrayList<ImportDefinition>();
	importExcelSources = new ArrayList<ExcelSource>();
    }

    public void load(String configFileName) throws IOException, SAXException, ParserConfigurationException {
	// Parse config xml
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	Document xmlDoc = dbf.newDocumentBuilder().parse(new FileInputStream(configFileName));

	LoaderDAO dao = new LoaderDAO();

	// Clear the database
	DataLoader dataLoader = new DataLoader();
	NodeList clearDatabaseNodeList = xmlDoc.getElementsByTagNameNS(NAMESPACE, "clearDatabase");
	if (clearDatabaseNodeList.getLength() > 0) {
	    LOGGER.info("Clearing database...");
	    dao.clearDatabase();
	}
	// Load initial config data
	NodeList loadDataNodeList = xmlDoc.getElementsByTagNameNS(NAMESPACE, "loadData");
	for (int i = 0; i < loadDataNodeList.getLength(); i++) {
	    Element loadDataNode = (Element) loadDataNodeList.item(i);
	    String fileName = loadDataNode.getAttribute("fileName");
	    LOGGER.info("Loading data file '" + fileName + "'...");
	    dataLoader.loadXml(new FileInputStream(fileName), loadDataNode.getAttribute("remark"));
	}

	dao.beginTransaction();

	// ImportHistory
	ImportHistory importHistory = new ImportHistory();
	importHistory.setDate(new Date());
	// TODO - Specify import remark
	importHistory.setRemark("REMARK");
	dao.save(importHistory);
	dao.setImportHistoryId(importHistory);

	// Fill look-up tables.
	kpiHolderMap = dao.getKpiHolderLeaves();

	parseConfig(xmlDoc);

	// Import data.
	importData();

	// Aggregate
	LOGGER.info("Aggregating...");
	Aggregator aggregator = new Aggregator(dao);
	for (KpiHolder kpiHolderRoot : dao.getRootKpiHolders()) {
	    aggregator.aggregate(kpiHolderRoot);
	}

	LOGGER.info("Saving data to database...");
	dao.commitTransaction();

	LOGGER.info("Done!");
    }

    private void parseConfig(Document xmlDoc) {
	// ImportDefinition
	parseImportDefinitions(xmlDoc.getDocumentElement(), null);
	// ExcelSource
	parseExcelSources(xmlDoc.getDocumentElement(), null);
    }

    private boolean parseImportDefinitions(Element parentElement, ImportDefinition parentImportDefinition) {
	boolean parsedSomething = false;
	// ImportDefinition
	NodeList importNodeList = parentElement.getElementsByTagNameNS(NAMESPACE, "importDefinition");
	for (int i = 0; i < importNodeList.getLength(); i++) {
	    Element importNode = (Element) importNodeList.item(i);
	    if (importNode.getParentNode().equals(parentElement)) {
		ImportDefinition importDefinition = new ImportDefinition(parentImportDefinition);
		importDefinition.setKpiVariableName(importNode.getAttribute("kpiVariableName"));
		importDefinition.setKpiHolderName(importNode.getAttribute("kpiHolderName"));
		importDefinition.setBeginDate(toDate(importNode.getAttribute("beginDate")));
		importDefinition.setEndDate(toDate(importNode.getAttribute("endDate")));
		importDefinition.setValue(toFloat(importNode.getAttribute("value")));

		boolean hasChildData = false;
		// Child ImportDefinitions
		hasChildData |= parseImportDefinitions(importNode, importDefinition);
		// ExcelSource
		hasChildData |= parseExcelSources(importNode, importDefinition);
		if (!hasChildData) {
		    importDefinitions.add(importDefinition);
		}

		parsedSomething = true;
	    }
	}
	return parsedSomething;
    }

    private boolean parseExcelSources(Element parentNode, ImportDefinition parentImportDefinition) {
	boolean parsedSomething = false;
	// ExcelSource
	NodeList excelSourceNodeList = parentNode.getElementsByTagNameNS(NAMESPACE, "excelSource");
	for (int i = 0; i < excelSourceNodeList.getLength(); i++) {
	    Element excelSourceNode = (Element) excelSourceNodeList.item(i);
	    if (excelSourceNode.getParentNode().equals(parentNode)) {
		String fileName = excelSourceNode.getAttribute("fileName");
		String sheetName = excelSourceNode.getAttribute("sheetName");
		String firstRow = excelSourceNode.getAttribute("firstRow");
		String lastRow = excelSourceNode.getAttribute("lastRow");
		ExcelSource excelSource = new ExcelSource(parentImportDefinition, fileName, sheetName, toInt(firstRow,
			-1), toInt(lastRow, -1));

		NodeList childNodes = excelSourceNode.getChildNodes();
		for (int k = 0; k < childNodes.getLength(); k++) {
		    Node childNode = childNodes.item(k);
		    if ((childNode instanceof Element) && (childNode.getNamespaceURI().equals(NAMESPACE))) {
			Element childElement = (Element) childNode;
			String nodeName = childElement.getLocalName();
			if (nodeName.equals("kpiVariableName")) {
			    // KpiVariable
			    String column = childElement.getAttribute("column");
			    String separator = childElement.getAttribute("separator");
			    excelSource.setKpiVariableNameObtainer(new ExcelObtainerName(column, separator));
			} else if (nodeName.equals("kpiHolderName")) {
			    // KpiHolder
			    String column = childElement.getAttribute("column");
			    String separator = childElement.getAttribute("separator");
			    excelSource.setKpiHolderNameObtainer(new ExcelObtainerName(column, separator));
			} else if (nodeName.equals("beginDate")) {
			    // BeginDate
			    String column = childElement.getAttribute("column");
			    excelSource.setBeginDateObtainer(new ExcelObtainerDate(column));
			} else if (nodeName.equals("endDate")) {
			    // EndDate
			    String column = childElement.getAttribute("column");
			    excelSource.setEndDateObtainer(new ExcelObtainerDate(column));
			} else if (nodeName.equals("period")) {
			    // Period
			    String column = childElement.getAttribute("column");
			    String year = childElement.getAttribute("year");
			    excelSource.setPeriodObtainer(new ExcelObtainerPeriod(column, toInt(year, -1)));
			} else if (nodeName.equals("value")) {
			    // Value
			    String column = childElement.getAttribute("column");
			    excelSource.setValueObtainer(new ExcelObtainerValue(column));
			} else {
			    throw new RuntimeException("Unknown node: <" + nodeName + ">");
			}
		    }
		}

		importExcelSources.add(excelSource);
		parsedSomething = true;
	    }
	}
	return parsedSomething;
    }

    private void importData() {
	Map<String, KpiVariable> kpiVariableMap = new HashMap<String, KpiVariable>();
	for (KpiVariable kpiVariable : dao.getKpiVariables()) {
	    kpiVariableMap.put(kpiVariable.getName(), kpiVariable);
	}

	KpiHolder kpiHolderRoot = dao.getKpiHolderByName("PORTFOLIO");
	KpiHolder kpiHolderDefaultParent = dao.getKpiHolderByName("DEFAULT");

	for (ImportDefinition importDefinition : importDefinitions) {
	    String kpiVariableName = importDefinition.getKpiVariableName();
	    KpiVariable kpiVariable = kpiVariableMap.get(kpiVariableName);
	    if (kpiVariable == null) {
		throw new RuntimeException("Undefined KpiVariable: " + kpiVariableName);
	    }
	    String kpiHolderName = importDefinition.getKpiHolderName();
	    KpiHolder kpiHolder = kpiHolderMap.get(kpiHolderName);
	    if (kpiHolder == null) {
		kpiHolder = dao.getOrCreateKpiHolder(kpiHolderName, kpiHolderRoot.getGuiLayout(),
			kpiHolderDefaultParent);
		kpiHolderMap.put(kpiHolderName, kpiHolder);
	    }
	    Period period = importDefinition.getPeriod();
	    float value = importDefinition.getValue();
	    dao.addDatedValue(kpiHolder, kpiVariable, value, period);
	}

	for (ExcelSource importExcelSource : importExcelSources) {
	    LOGGER.info("Reading Excel file '" + importExcelSource.getFileName() + "' sheet '"
		    + importExcelSource.getSheetName() + "'...");
	    for (int row = importExcelSource.getFirstRow(); row <= importExcelSource.getLastRow(); row++) {
		try {
		    // TODO - import hierarchy

		    // Import data.
		    String kpiVariableName = importExcelSource.getKpiVariableName(row);
		    KpiVariable kpiVariable = kpiVariableMap.get(kpiVariableName);
		    if (kpiVariable == null) {
			throw new RuntimeException("Undefined KpiVariable: " + kpiVariableName);
		    }
		    String kpiHolderName = importExcelSource.getKpiHolderName(row);
		    KpiHolder kpiHolder = kpiHolderMap.get(kpiHolderName);
		    if (kpiHolder == null) {
			kpiHolder = dao.getOrCreateKpiHolder(kpiHolderName, kpiHolderRoot.getGuiLayout(),
				kpiHolderDefaultParent);
			kpiHolderMap.put(kpiHolderName, kpiHolder);
		    }
		    Period period = importExcelSource.getPeriod(row);
		    float value = importExcelSource.getValue(row);
		    dao.addDatedValue(kpiHolder, kpiVariable, value, period);
		} catch (RuntimeException e) {
		    LOGGER.error("Excel row " + row + ": " + e.getMessage());
		}
	    }
	}
    }
}