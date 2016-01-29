package nl.valori.dashboard.dataloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.valori.dashboard.dao.DAO;
import nl.valori.dashboard.dao.HibernateDAOImpl;
import nl.valori.dashboard.model.DatedValue;
import nl.valori.dashboard.model.GuiComponent;
import nl.valori.dashboard.model.GuiElement;
import nl.valori.dashboard.model.GuiLayout;
import nl.valori.dashboard.model.ImportHistory;
import nl.valori.dashboard.model.KpiHolder;
import nl.valori.dashboard.model.KpiVariable;
import nl.valori.dashboard.model.Property;
import nl.valori.dashboard.model.PropertyHolder;
import nl.valori.dashboard.model.Threshold;
import nl.valori.dashboard.model.ValueSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataLoader extends XmlUtil {

    private static final String NAMESPACE = "http://www.valori.nl/dashboard/dataLoader";

    public static void main(String[] args) {
	if (args.length == 0) {
	    System.out.println("Usage: " + DataLoader.class.getSimpleName() + "<xmlfile> [<xmlfile> ...] ");
	    System.exit(1);
	}
	DataLoader dataLoader = new DataLoader();
	for (String arg : args) {
	    try {
		dataLoader.loadXml(new FileInputStream(args[0]), DataLoader.class.getSimpleName() + ": " + args[0]);
	    } catch (Exception e) {
		System.err.println("Error loading file " + arg);
		e.printStackTrace();
	    }
	}
    }

    private Map<String, KpiVariable> kpiVariableMap;
    private Map<String, GuiLayout> guiLayoutMap;
    private Map<String, KpiHolder> kpiHolderMap;

    public DataLoader() {
	kpiVariableMap = new HashMap<String, KpiVariable>();
	guiLayoutMap = new HashMap<String, GuiLayout>();
	kpiHolderMap = new HashMap<String, KpiHolder>();
    }

    public long loadXml(InputStream inputStream, String importRemark) throws IOException, SAXException {
	kpiVariableMap.clear();
	guiLayoutMap.clear();

	Document xmlDoc = getDocumentBuilder().parse(inputStream);

	DAO dao = new HibernateDAOImpl();
	try {
	    dao.beginTransaction();

	    // ImportHistory
	    ImportHistory importHistory = new ImportHistory();
	    importHistory.setDate(new Date());
	    importHistory.setRemark(importRemark);
	    dao.save(importHistory);

	    // KpiVariable
	    NodeList kpiVariableNodeList = xmlDoc.getElementsByTagNameNS(NAMESPACE, "kpiVariable");
	    for (int i = 0; i < kpiVariableNodeList.getLength(); i++) {
		Element kpiVariableNode = (Element) kpiVariableNodeList.item(i);
		KpiVariable kpiVariable = new KpiVariable();
		kpiVariable.setName(kpiVariableNode.getAttribute("name"));
		kpiVariable.setAccumulated(toBoolean(kpiVariableNode.getAttribute("accumulated"), false));
		kpiVariable
			.setWeightedAggregation(toBoolean(kpiVariableNode.getAttribute("weightedAggregation"), false));
		kpiVariable.setInterpolated(toBoolean(kpiVariableNode.getAttribute("interpolated"), false));

		// Threshold
		NodeList thresholdNodeList = kpiVariableNode.getElementsByTagNameNS(NAMESPACE, "threshold");
		for (int k = 0; k < thresholdNodeList.getLength(); k++) {
		    Element thresholdNode = (Element) thresholdNodeList.item(k);
		    Threshold threshold = new Threshold();
		    threshold.setName(thresholdNode.getAttribute("name"));
		    threshold.setAlertLevelIfAbove(toInt(thresholdNode.getAttribute("alertIfAbove"), 0));
		    threshold.setAlertLevelIfAt(toInt(thresholdNode.getAttribute("alertIfAt"), 0));
		    threshold.setAlertLevelIfBelow(toInt(thresholdNode.getAttribute("alertIfBelow"), 0));
		    threshold.setFractionOfKpiVariable(toKpiVariable(thresholdNode
			    .getAttribute("fractionOfKpiVariable")));
		    threshold.setRelativeToKpiVariable(toKpiVariable(thresholdNode
			    .getAttribute("relativeToKpiVariable")));
		    threshold.setValue(toFloat(thresholdNode.getAttribute("value")));

		    dao.save(threshold);
		    kpiVariable.addThresholds(threshold);
		}

		kpiVariableMap.put(kpiVariable.getName(), kpiVariable);
		dao.save(kpiVariable);
	    }

	    // GuiLayout
	    NodeList guiLayoutNodeList = xmlDoc.getElementsByTagNameNS(NAMESPACE, "guiLayout");
	    for (int i = 0; i < guiLayoutNodeList.getLength(); i++) {
		Element guiLayoutNode = (Element) guiLayoutNodeList.item(i);
		GuiLayout guiLayout = new GuiLayout();
		guiLayout.setName(guiLayoutNode.getAttribute("name"));

		// Properties
		loadProperties(dao, guiLayoutNode, guiLayout);

		// GuiComponent
		NodeList guiComponentNodeList = guiLayoutNode.getElementsByTagNameNS(NAMESPACE, "guiComponent");
		for (int j = 0; j < guiComponentNodeList.getLength(); j++) {
		    Element guiComponentNode = (Element) guiComponentNodeList.item(j);
		    GuiComponent guiComponent = new GuiComponent();
		    guiComponent.setType(toGuiComponentType(guiComponentNode.getAttribute("type")));
		    guiComponent.setTitle(guiComponentNode.getAttribute("title"));
		    guiComponent.setLayout(guiComponentNode.getAttribute("layout"));

		    // Properties
		    loadProperties(dao, guiComponentNode, guiComponent);

		    // GuiElementPlanned
		    NodeList guiElementNodeList = guiComponentNode.getElementsByTagNameNS(NAMESPACE, "guiElement");
		    for (int k = 0; k < guiElementNodeList.getLength(); k++) {
			Element guiElementNode = (Element) guiElementNodeList.item(k);
			GuiElement guiElement = new GuiElement();
			guiElement.setRepresents(guiElementNode.getAttribute("represents"));
			guiElement.setKpiVariable(toKpiVariable(guiElementNode.getAttribute("kpiVariable")));
			guiElement.setLabel(guiElementNode.getAttribute("label"));
			guiElement.setValueFormat(guiElementNode.getAttribute("valueFormat"));
			guiElement.setUnit(guiElementNode.getAttribute("unit"));

			// Properties
			loadProperties(dao, guiElementNode, guiElement);

			dao.save(guiElement);
			guiComponent.getGuiElements().add(guiElement);
		    }

		    dao.save(guiComponent);
		    guiLayout.getGuiComponents().add(guiComponent);
		}

		guiLayoutMap.put(guiLayout.getName(), guiLayout);
		dao.save(guiLayout);
	    }

	    // KpiHolder
	    NodeList kpiHolderNodeList = xmlDoc.getDocumentElement().getElementsByTagNameNS(NAMESPACE, "kpiHolder");
	    for (int i = 0; i < kpiHolderNodeList.getLength(); i++) {
		Element kpiHolderNode = (Element) kpiHolderNodeList.item(i);
		KpiHolder kpiHolder = new KpiHolder();
		kpiHolder.setName(kpiHolderNode.getAttribute("name"));
		kpiHolder.setGuiLayout(toGuiLayout(kpiHolderNode.getAttribute("guiLayout")));
		KpiHolder parent = toKpiHolder(kpiHolderNode.getAttribute("parent"));
		kpiHolder.setParent(parent);
		if (parent != null) {
		    parent.getChildren().add(kpiHolder);
		}

		// ValueSet
		NodeList valueSetNodeList = kpiHolderNode.getElementsByTagNameNS(NAMESPACE, "valueSet");
		for (int j = 0; j < valueSetNodeList.getLength(); j++) {
		    Element valueSetNode = (Element) valueSetNodeList.item(j);
		    ValueSet valueSet = new ValueSet();
		    valueSet.setWeightFactor(toFloat(valueSetNode.getAttribute("weightFactor"), 1.0F));
		    valueSet.setKpiVariable(toKpiVariable(valueSetNode.getAttribute("kpiVariable")));

		    // Value
		    NodeList datedValueNodeList = valueSetNode.getElementsByTagNameNS(NAMESPACE, "value");
		    for (int k = 0; k < datedValueNodeList.getLength(); k++) {
			Element datedValueNode = (Element) datedValueNodeList.item(k);
			DatedValue datedValue = new DatedValue();
			datedValue.setBeginDate(toDate(datedValueNode.getAttribute("begin")));
			datedValue.setEndDate(toDate(datedValueNode.getAttribute("end")));
			datedValue.setValue(toFloat(datedValueNode.getAttribute("value")));
			datedValue.setImportHistory(importHistory);
			dao.save(datedValue);
			valueSet.addDatedValues(datedValue);
		    }

		    dao.save(valueSet);
		    kpiHolder.getValueSets().add(valueSet);
		}

		kpiHolderMap.put(kpiHolder.getCode(), kpiHolder);
		dao.save(kpiHolder);
	    }
	    dao.commitTransaction();

	    return importHistory.getId();
	} catch (RuntimeException e) {
	    try {
		dao.rollbackTransaction();
	    } catch (Throwable t) {
		// Ignore rollback exceptions
	    }
	    throw e;
	}
    }

    private void loadProperties(DAO dao, Element element, PropertyHolder propertyHolder) {
	NodeList propertyNodeList = element.getElementsByTagNameNS(NAMESPACE, "property");
	for (int i = 0; i < propertyNodeList.getLength(); i++) {
	    Element propertyNode = (Element) propertyNodeList.item(i);
	    // Prevent deeper nested "property"-nodes from being loaded here; only load nodes that are direct children.
	    if (propertyNode.getParentNode().equals(element)) {
		Property property = new Property();
		property.setName(propertyNode.getAttribute("name"));
		property.setValue(propertyNode.getAttribute("value"));

		propertyHolder.getProperties().add(property);
		dao.save(property);
	    }
	}
    }

    private String toGuiComponentType(String value) {
	if ((value == null) || (value.length() == 0)) {
	    return null;
	} else {
	    return GuiComponent.Type.valueOf(value).name();
	}
    }

    private GuiLayout toGuiLayout(String value) {
	if ((value == null) || (value.length() == 0)) {
	    return null;
	}
	GuiLayout guiLayout = guiLayoutMap.get(value);
	if (guiLayout == null) {
	    throw new RuntimeException("GuiLayout not found: " + value);
	}
	return guiLayout;
    }

    private KpiHolder toKpiHolder(String value) {
	if ((value == null) || (value.length() == 0)) {
	    return null;
	}
	// TODO - Solve KpiHolder.code
	KpiHolder kpiHolder = kpiHolderMap.get(value.replaceAll("#.*", ""));
	return kpiHolder;
    }

    private KpiVariable toKpiVariable(String value) {
	if ((value == null) || (value.length() == 0)) {
	    return null;
	}
	KpiVariable kpiVariable = kpiVariableMap.get(value);
	if (kpiVariable == null) {
	    throw new RuntimeException("KpiVariable not found: " + value);
	}
	return kpiVariable;
    }
}