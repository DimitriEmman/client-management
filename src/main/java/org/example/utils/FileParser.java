package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.example.entities.Client;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FileParser {
    public static List<Client> parseFile(MultipartFile file) throws IOException {
        log.info("inside parseFile method");
        String fileName = file.getOriginalFilename();
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return parseExcel(file);
        } else if (fileName.endsWith(".csv")) {
            return parseCSV(file);
        } else if (fileName.endsWith(".xml")) {
            return parseXML(file);
        } else if (fileName.endsWith(".json")) {
            // Add JSON parsing logic here
            return parseJSON(file);
        }
        else if (fileName.endsWith(".txt")) {
            return parseTxt(file);

        }
        else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

    private static List<Client> parseCSV(MultipartFile file) throws IOException {
        log.info("inside parseCSV method");

        List<Client> people = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            csvReader.readNext();

            List<String[]> records = csvReader.readAll();
            log.info("try");

            for (String[] record : records) {
                Client client = new Client();
                client.setFirstname(record[0]);
                client.setLastname(record[1]);
                client.setAge(Integer.valueOf(record[2]));
                client.setProfession(record[3]);
                client.setSalary(Double.valueOf(record[4]));

                people.add(client);
            }
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
        return people;
    }

    private static List<Client> parseXML(MultipartFile file) throws IOException {
        log.info("inside parseXML method");

        List<Client> clients = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file.getInputStream());

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("client");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {

                Node node = nodeList.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    Client client = new Client();
                    client.setFirstname(element.getElementsByTagName("firstname").item(0).getTextContent());
                    client.setLastname(element.getElementsByTagName("lastname").item(0).getTextContent());
                    client.setAge(Integer.valueOf(element.getElementsByTagName("age").item(0).getTextContent()));
                    client.setProfession(element.getElementsByTagName("profession").item(0).getTextContent());
                    client.setSalary(Double.valueOf(element.getElementsByTagName("salary").item(0).getTextContent()));

                    clients.add(client);
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Error parsing XML file: " + e.getMessage());
        }
        return clients;
    }

    private static List<Client> parseExcel(MultipartFile file) throws IOException {
        log.info("inside parseExcel method");

        List<Client> clients = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }

                Client client = new Client();
                client.setFirstname(row.getCell(0).getStringCellValue());
                client.setLastname(row.getCell(1).getStringCellValue());
                client.setAge(Integer.valueOf(row.getCell(2).getStringCellValue()));
                client.setProfession(row.getCell(3).getStringCellValue());
                client.setSalary(Double.valueOf(row.getCell(4).getStringCellValue()));

                clients.add(client);
            }
        }
        return clients;
    }

    private static List<Client> parseJSON(MultipartFile file) throws IOException {
        log.info("inside parseJSON method");

        List<Client> clients = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());

            if (rootNode.has("persons") && rootNode.get("persons").isArray())
            {
                JsonNode personsArray = rootNode.get("persons");
                    for (JsonNode node : personsArray) {
                        Client client = new Client();
                        client.setFirstname(node.get("firstname").asText());
                        client.setLastname(node.get("lastname").asText());
                        client.setAge(Integer.valueOf(node.get("age").asText()));
                        client.setProfession(node.get("profession").asText());
                        client.setSalary(Double.valueOf(node.get("salary").asText()));

                        clients.add(client);
                    }
                }
            else {
                throw new IOException("Invalid JSON structure. Expected an array of objects under the key 'persons'.");
            }
        } catch (IOException e) {
            throw new IOException("Error parsing JSON file: " + e.getMessage());
        }
        return clients;
    }

    private static List<Client> parseTxt(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));

        String[] columnNames = reader.readLine().split("\t");

        return reader.lines().map(line -> {
            String[] values = line.split("\t");
            Client client = new Client();

            for (int i = 0; i < columnNames.length; i++) {
                switch (columnNames[i]) {
                    case "firstname":
                        client.setFirstname(values[i]);
                        break;
                    case "lastname":
                        client.setLastname(values[i]);
                        break;
                    case "age":
                        client.setAge(Integer.parseInt(values[i]));
                        break;
                    case "profession":
                        client.setProfession(values[i]);
                        break;
                    case "salary":
                        client.setSalary(Double.valueOf(values[i]));
                        break;
                }
            }

            return client;
        }).collect(Collectors.toList());
    }

}
