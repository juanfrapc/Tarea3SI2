package Model;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Grafo {

    private Model modelo;

    public Grafo(String path) {
        LogCtl.setCmdLogging();
        this.modelo = RDFDataMgr.loadModel(path);
    }

    public Grafo() {
        LogCtl.setCmdLogging();
        this.modelo = ModelFactory.createDefaultModel();
    }

    public String saveQueryResult(File file, String consultaStr) {
        try {
            ResultSet resultado = consultaLocal(consultaStr);
            ResultSetFormatter.outputAsXML(new BufferedOutputStream(new FileOutputStream(file)), resultado);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (QueryParseException ex) {
            return "Error en la consulta. Línea: " + ex.getLine() + ", Columna: " + ex.getColumn();
        }
        return "Resultado guardado en " + file.getAbsolutePath();
    }

    public String getQueryResult(String consultaStr) {
        try {
            ResultSet resultado = consultaLocal(consultaStr);
            return ResultSetFormatter.asText(resultado);
        } catch (QueryParseException ex) {
            return "Error en la consulta. Línea: " + ex.getLine() + ", Columna: " + ex.getColumn();
        }
    }

    private ResultSet consultaLocal(String consultaStr) throws QueryParseException {
        ResultSet resultados = null;
        Query consulta = QueryFactory.create(consultaStr);
        try (QueryExecution ejecucion = QueryExecutionFactory.create(consulta, this.modelo)) {
            resultados = ejecucion.execSelect();
            resultados = ResultSetFactory.copyResults(resultados);
        }
        return resultados;
    }

    public static String getRemoteQueryResult(String service, String consultaStr) {
        try {
            ResultSet resultado = consultaRemota(service, consultaStr);
            return ResultSetFormatter.asText(resultado);
        } catch (QueryParseException ex) {
            return "Error en la consulta. Línea: " + ex.getLine() + ", Columna: " + ex.getColumn();
        }
    }

    public static String saveRemoteQueryResult(File file, String service, String consultaStr) {
        try {
            ResultSet resultado = consultaRemota(service, consultaStr);
            ResultSetFormatter.outputAsXML(new BufferedOutputStream(new FileOutputStream(file)), resultado);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (QueryParseException ex) {
            return "Error en la consulta. Línea: " + ex.getLine() + ", Columna: " + ex.getColumn();
        }
        return "Resultado guardado en " + file.getAbsolutePath();
    }

    private static ResultSet consultaRemota(String service, String consultaStr) throws QueryParseException {
        ResultSet resultados = null;
        Query consulta = QueryFactory.create(consultaStr);
        try (QueryExecution ejecucion = QueryExecutionFactory.sparqlService(service, consulta)) {
            resultados = ejecucion.execSelect();
            resultados = ResultSetFactory.copyResults(resultados);
        }
        return resultados;
    }
}
