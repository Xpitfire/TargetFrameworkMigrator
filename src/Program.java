import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by xpitfire on 28/05/2017.
 */
public class Program {

    private static final String LOGGER_FILE_NAME = "TargetFrameworkMigrator.log";

    private static Logger logger;

    private static String path = null;
    private static boolean recursive = false;
    private static String version = null;

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        initLogger();

        try {
            if (args == null
                    || args.length < 1) {
                System.err.println("Invalid parameter list!");
                System.err.println("Missing path and text prefix.");
                printErrorHelp();
                return;
            }

            Map<String, String> paramMap = new HashMap<>();

            int idx = 0;
            System.out.printf("Found %s parameters!\n", args.length);
            while (idx < args.length) {
                String[] keyValue = args[idx].split("=");
                if (keyValue.length != 2) {
                    idx++;
                    continue;
                }
                paramMap.put(keyValue[0], keyValue[1]);
                idx++;
            }

            // mandatory settings

            if (paramMap.containsKey("-path")) {
                path = paramMap.get("-path");
            } else {
                System.err.println("Invalid version parameter!");
                printErrorHelp();
                return;
            }

            if (paramMap.containsKey("-version")) {
                version = paramMap.get("-version");
            } else {
                System.err.println("Invalid path parameter!");
                printErrorHelp();
                return;
            }

            // optional settings

            if (paramMap.containsKey("-recursive")) {
                recursive = Boolean.parseBoolean(paramMap.get("-recursive"));
            }
            
            System.out.printf("Initialized path=%s, version=%s recursive=%b\n", path, version, recursive);
            final File folder = new File(path);

            System.out.println("Scanning directories...");
            scanAndUpdateFiles(folder);

        } catch (Exception ex) {

            System.err.println("Application crashed: " + ex.getMessage());
            logger.log(Level.SEVERE, "Application crashed", ex);

        }
    }

    private static void printErrorHelp() {
        System.err.println("usage> java TargetFrameworkMigrator.jar -path=<path-to-root-dir> -version=<version-string> -recursive=<true/false>");
        System.err.println("example> java TargetFrameworkMigrator.jar -path=C:\\test -version=v4.6.2 -recursive=true");
        logger.severe("Invalid application parameter> " +
            String.format("-path=%s, -version=%s, -recursive=%b -\n",
                    path, version, recursive));
    }

    private static void scanAndUpdateFiles(final File folder)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException {

        File[] files = folder.listFiles();
        if (files == null) return;

        System.out.println(folder + ":");
        for (final File fileEntry : files) {
            if (fileEntry.equals(folder)) return;

            if (fileEntry.isDirectory() && recursive) {
                scanAndUpdateFiles(fileEntry);
            } else if (fileEntry.isFile()) {
                String file = fileEntry.getName();
                if (getFileExtension(file).equals(".csproj")) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(fileEntry);
                    XPathFactory xPathfactory = XPathFactory.newInstance();
                    XPath xpath = xPathfactory.newXPath();
                    XPathExpression expr = xpath.compile("/Project/PropertyGroup/TargetFrameworkVersion/text()");
                    NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
                    if (nodeList.getLength() < 1) continue;
                    Node node = nodeList.item(0);
                    logger.info("Found: " + fileEntry.getAbsolutePath() + " version:" + node.getTextContent());
                    node.setTextContent(version);
                    Transformer xformer = TransformerFactory.newInstance().newTransformer();
                    xformer.transform(new DOMSource(doc), new StreamResult(new File(fileEntry.getAbsolutePath())));
                    logger.info("Changed: " + fileEntry.getAbsolutePath() + " version:" + node.getTextContent());
                }
            }
        }
        System.out.println();
    }

    private static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = "." + fileName.substring(i + 1);
        }
        return extension;
    }

    private static void initLogger() {
        logger = Logger.getLogger("TargetFrameworkMigrator");
        FileHandler fh;
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler(LOGGER_FILE_NAME, true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

}
