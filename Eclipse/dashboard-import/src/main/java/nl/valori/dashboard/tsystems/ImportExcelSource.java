package nl.valori.dashboard.tsystems;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.valori.dashboard.model.KpiVariable;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ImportExcelSource {

    private String fileName;
    private String sheetName;
    private int firstRow;
    private int lastRow;
    private Map<KpiVariable, ImportExcelDefinition> importDefinitionMap;
    private Set<ImportExcelDefinition> kpiHolderParents;

    public ImportExcelSource(String fileName, String sheetName, int firstRow, int lastRow) {
	this.fileName = fileName;
	this.sheetName = sheetName;
	this.firstRow = firstRow;
	this.lastRow = lastRow;
	importDefinitionMap = new HashMap<KpiVariable, ImportExcelDefinition>();
	kpiHolderParents = new HashSet<ImportExcelDefinition>();
    }

    public String getFileName() {
	return fileName;
    }

    public String getSheetName() {
	return sheetName;
    }

    public int getFirstRow() {
	return firstRow;
    }

    public int getLastRow() {
	return lastRow;
    }

    public void put(KpiVariable kpiVariable, ImportExcelDefinition importDefinition) {
	importDefinitionMap.put(kpiVariable, importDefinition);
    }

    public Set<KpiVariable> getKpiVariables() {
	return importDefinitionMap.keySet();
    }

    public ImportExcelDefinition getImportExcelDefinition(KpiVariable kpiVariable) {
	// TODO - KpiVariable.equals()
	return importDefinitionMap.get(kpiVariable);
    }

    public void add(ImportExcelDefinition importDefinition) {
	kpiHolderParents.add(importDefinition);
    }

    public Set<ImportExcelDefinition> getKpiHolderParents() {
	return kpiHolderParents;
    }

    public HSSFSheet readSheet() {
	try {
	    return new HSSFWorkbook(new FileInputStream(fileName)).getSheet(sheetName);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }
}