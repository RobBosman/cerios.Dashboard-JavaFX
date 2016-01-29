package nl.valori.dashboard.tsystems;

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
import nl.valori.dashboard.loader.Aggregator;
import nl.valori.dashboard.loader.LoaderDAO;
import nl.valori.dashboard.model.ImportHistory;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.KpiVariable;
import nl.valori.dashboard.model.Period;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TSystems_Import extends XmlUtil {

    private static final String NAMESPACE = "http://www.valori.nl/dashboard/import";
    private static final Logger LOGGER = Logger.getLogger(TSystems_Import.class);

    /**
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
	if (args.length != 1) {
	    LOGGER.error("Usage: " + TSystems_Import.class.getSimpleName() + " <importConfig.xml>");
	    System.exit(1);
	}
	new TSystems_Import().load(args[0]);
    }

    private LoaderDAO dao;
    private List<ImportExcelSource> importExcelSources;
    private Map<String, KpiHolder> kpiHolderMap;
    private Map<String, KpiVariable> kpiVariableMap;

    public TSystems_Import() {
	importExcelSources = new ArrayList<ImportExcelSource>();
	kpiVariableMap = new HashMap<String, KpiVariable>();
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
	// Excel
	NodeList excelNodeList = xmlDoc.getElementsByTagNameNS(NAMESPACE, "excel");
	for (int i = 0; i < excelNodeList.getLength(); i++) {
	    Element excelNode = (Element) excelNodeList.item(i);
	    String fileName = excelNode.getAttribute("fileName");
	    String sheetName = excelNode.getAttribute("sheetName");
	    String firstRow = excelNode.getAttribute("firstRow");
	    String lastRow = excelNode.getAttribute("lastRow");
	    ImportExcelSource importExcelSource = new ImportExcelSource(fileName, sheetName, toInt(firstRow, -1),
		    toInt(lastRow, -1));

	    // KpiVariable
	    NodeList kpiVariableNodeList = excelNode.getElementsByTagNameNS(NAMESPACE, "kpiVariable");
	    for (int j = 0; j < kpiVariableNodeList.getLength(); j++) {
		Element kpiVariableNode = (Element) kpiVariableNodeList.item(j);
		String name = kpiVariableNode.getAttribute("name");
		KpiVariable kpiVariable = kpiVariableMap.get(name);
		if (kpiVariable == null) {
		    kpiVariable = dao.getKpiVariableByName(name);
		    kpiVariableMap.put(name, kpiVariable);
		}

		ImportExcelDefinition importExcelDefinition = new ImportExcelDefinition();

		NodeList childNodes = kpiVariableNode.getChildNodes();
		for (int k = 0; k < childNodes.getLength(); k++) {
		    Node childNode = childNodes.item(k);
		    if ((childNode instanceof Element) && (childNode.getNamespaceURI().equals(NAMESPACE))) {
			Element childElement = (Element) childNode;
			String nodeName = childElement.getLocalName();
			if (nodeName.equals("kpiHolderName")) {
			    // KpiHolder
			    String column = childElement.getAttribute("column");
			    String separator = childElement.getAttribute("separator");
			    importExcelDefinition.setKpiHolderNameObtainer(new ImportExcelObtainerName(column,
				    separator));
			} else if (nodeName.equals("value")) {
			    // Value
			    String column = childElement.getAttribute("column");
			    importExcelDefinition.setValueObtainer(new ImportExcelObtainerValue(column));
			} else if (nodeName.equals("period")) {
			    // Period
			    String column = childElement.getAttribute("column");
			    String year = childElement.getAttribute("year");
			    importExcelDefinition.setPeriodObtainer(new ImportExcelObtainerPeriod(column, toInt(year,
				    -1)));
			} else if (nodeName.equals("beginDate")) {
			    // BeginDate
			    String column = childElement.getAttribute("column");
			    importExcelDefinition.setBeginDateObtainer(new ImportExcelObtainerDate(column));
			} else if (nodeName.equals("endDate")) {
			    // EndDate
			    String column = childElement.getAttribute("column");
			    importExcelDefinition.setEndDateObtainer(new ImportExcelObtainerDate(column));
			}
		    }
		}

		importExcelSource.put(kpiVariable, importExcelDefinition);
	    }

	    // KpiHolderHierarchy
	    NodeList kpiHolderHierarchyNodeList = excelNode.getElementsByTagNameNS(NAMESPACE, "kpiHolderHierarchy");
	    for (int j = 0; j < kpiHolderHierarchyNodeList.getLength(); j++) {
		Element kpiHolderHierarchyNode = (Element) kpiHolderHierarchyNodeList.item(j);

		ImportExcelDefinition importExcelDefinition = new ImportExcelDefinition();

		NodeList childNodes = kpiHolderHierarchyNode.getChildNodes();
		for (int k = 0; k < childNodes.getLength(); k++) {
		    Node childNode = childNodes.item(k);
		    if ((childNode instanceof Element) && (childNode.getNamespaceURI().equals(NAMESPACE))) {
			Element childElement = (Element) childNode;
			String nodeName = childElement.getLocalName();
			if (nodeName.equals("kpiHolderName")) {
			    // KpiHolder
			    String column = childElement.getAttribute("column");
			    String separator = childElement.getAttribute("separator");
			    importExcelDefinition.setKpiHolderNameObtainer(new ImportExcelObtainerName(column,
				    separator));
			} else if (nodeName.equals("kpiHolderParentName")) {
			    // KpiHolderParent
			    String column = childElement.getAttribute("column");
			    String separator = childElement.getAttribute("separator");
			    importExcelDefinition.setKpiHolderParentNameObtainer(new ImportExcelObtainerName(column,
				    separator));
			}
		    }
		}

		importExcelSource.add(importExcelDefinition);
	    }

	    importExcelSources.add(importExcelSource);
	}
    }

    private void importData() {
	KpiHolder kpiHolderRoot = dao.getKpiHolderByName("PORTFOLIO");
	KpiHolder kpiHolderDefaultParent = dao.getKpiHolderByName("DEFAULT");

	for (ImportExcelSource importExcelSource : importExcelSources) {
	    LOGGER.info("Reading Excel file '" + importExcelSource.getFileName() + "' sheet '"
		    + importExcelSource.getSheetName() + "'...");
	    HSSFSheet sheet = importExcelSource.readSheet();
	    for (int r = importExcelSource.getFirstRow(); r <= importExcelSource.getLastRow(); r++) {
		HSSFRow row = sheet.getRow(r - 1);
		if (row != null) {
		    try {
			// Import hierarchy info
			for (ImportExcelDefinition importExcelDefinition : importExcelSource.getKpiHolderParents()) {
			    String kpiHolderName = importExcelDefinition.getKpiHolderName(row);
			    String kpiHolderCode = kpiHolderName.replaceAll("#.*", "");
			    if (kpiHolderName.length() != 0) {
				KpiHolder kpiHolderParent;
				String kpiHolderParentCode = importExcelDefinition.getKpiHolderParentName(row);
				if ((kpiHolderParentCode == null) || (kpiHolderParentCode.length() == 0)) {
				    kpiHolderParent = kpiHolderRoot;
				} else {
				    kpiHolderParent = kpiHolderMap.get(kpiHolderParentCode);
				    if (kpiHolderParent == null) {
					kpiHolderParent = dao.getOrCreateKpiHolder(kpiHolderParentCode, kpiHolderRoot
						.getGuiLayout(), kpiHolderRoot);
					kpiHolderMap.put(kpiHolderParentCode, kpiHolderParent);
				    }
				}

				KpiHolder kpiHolder = kpiHolderMap.get(kpiHolderName);
				if (kpiHolder == null) {
				    kpiHolder = dao.getOrCreateKpiHolder(kpiHolderName, kpiHolderRoot.getGuiLayout(),
					    kpiHolderParent);
				    kpiHolderMap.put(kpiHolderCode, kpiHolder);
				} else if ((kpiHolder.getParent() != null)
					&& (!kpiHolder.getParent().getCode().equals(kpiHolderParentCode))) {
				    if (kpiHolderParent != null) {
					kpiHolderParent.getChildren().add(kpiHolder);
				    }
				    kpiHolder.setParent(kpiHolderParent);
				    dao.save(kpiHolder);
				}
			    }
			}

			// Import data for KpiVariables
			for (KpiVariable kpiVariable : importExcelSource.getKpiVariables()) {
			    ImportExcelDefinition importExcelDefinition = importExcelSource
				    .getImportExcelDefinition(kpiVariable);
			    String kpiHolderName = importExcelDefinition.getKpiHolderName(row);
			    String code = kpiHolderName.replaceAll("#.*", "");
			    KpiHolder kpiHolder = kpiHolderMap.get(code);
			    if (kpiHolder == null) {
				kpiHolder = dao.getOrCreateKpiHolder(kpiHolderName, kpiHolderRoot.getGuiLayout(),
					kpiHolderDefaultParent);
				kpiHolderMap.put(code, kpiHolder);
			    }
			    float value = importExcelDefinition.getValue(row);
			    Period period = importExcelDefinition.getPeriod(row);
			    dao.addDatedValue(kpiHolder, kpiVariable, value, period);
			}
		    } catch (RuntimeException e) {
			LOGGER.error("Excel row " + r + ": " + e.getMessage());
		    }
		}
	    }
	}
    }
}