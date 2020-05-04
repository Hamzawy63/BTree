package eg.edu.alexu.csd.filestructure.btree;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchEngine implements ISearchEngine {

    BTree<String, Map<String, Integer>> btree;

    public SearchEngine(int t) {
        btree = new BTree<>(t);
    }

    @Override
    public void indexWebPage(String filePath) {
        InputChecker.checkNullValue(filePath);
        InputChecker.checkEmptyString(filePath);

        File inputFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
            LocalException.throwRunTimeErrorException();
        }
        doc.getDocumentElement().normalize();
        NodeList list = doc.getElementsByTagName("doc");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            String id = node.getAttributes().getNamedItem("id").getNodeValue();
            String[] contents = reformInput(node.getTextContent()).split(" ");
            Map<String, Integer> wordsFreq = new HashMap<>();
            for (String word : contents) {
                if (wordsFreq.containsKey(word)) {
                    wordsFreq.replace(word, wordsFreq.get(word) + 1);
                } else {
                    wordsFreq.put(word, 1);
                }
            }
            for (Map.Entry<String, Integer> it : wordsFreq.entrySet()) {
                String word = it.getKey();
                Integer rank = it.getValue();
                Map<String, Integer> mp = btree.search(word);
                if (mp == null) {
                    mp = new HashMap<>();
                    mp.put(id, rank);
                    btree.insert(word, mp);
                } else {
                    mp.put(id, rank);
                }
            }
        }


    }

    @Override
    public void indexDirectory(String directoryPath) {
        InputChecker.checkNullValue(directoryPath);
        try (Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            // We want to find only regular files
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
            for (String file : result) {
                indexWebPage(file);
            }
        } catch (IOException e) {
            System.out.println("Error in reading Directory " + directoryPath);
            e.printStackTrace();
            LocalException.throwRunTimeErrorException();
        }
    }

    @Override
    public void deleteWebPage(String filePath) {
        InputChecker.checkNullValue(filePath);
        InputChecker.checkEmptyString(filePath);

        File inputFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
            LocalException.throwRunTimeErrorException();
        }
        doc.getDocumentElement().normalize();
        NodeList list = doc.getElementsByTagName("doc");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            String id = node.getAttributes().getNamedItem("id").getNodeValue();
            String[] contents = reformInput(node.getTextContent()).split(" ");
            for (String word : contents) {
                Map<String, Integer> mp = btree.search(word);
                if (mp != null)
                    mp.remove(id) ;
            }
        }
    }

    @Override
    public List<ISearchResult> searchByWordWithRanking(String word) {
        InputChecker.checkNullValue(word);
        word = word.toLowerCase();
        List<ISearchResult> result = new ArrayList<>();
        Map<String, Integer> cachedResult = btree.search(word);
        if (word != null && cachedResult != null) {
            for (Map.Entry<String, Integer> it : cachedResult.entrySet()) {
                result.add(new SearchResult(it.getKey(), it.getValue()));
            }
        }
        return result;
    }

    @Override
    public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
        InputChecker.checkNullValue(sentence);
        sentence = sentence.toLowerCase();
        String[] words = reformInput(sentence).split(" ");
        Map<String, Integer> res = new HashMap<>();
        for (String word : words) {
            Map<String, Integer> cachedResult = btree.search(word);
            if (cachedResult == null) {
            } else {
                for (Map.Entry<String, Integer> it : cachedResult.entrySet()) {
                    String key = it.getKey();
                    if (res.containsKey(key)) {
                        res.replace(key, Math.min(it.getValue(), res.get(key)));
                    } else {
                        res.put(key, it.getValue());
                    }
                }
            }
        }
        List<ISearchResult> list = new ArrayList<>();
        for (Map.Entry<String, Integer> it : res.entrySet()) {
            list.add(new SearchResult(it.getKey(), it.getValue()));
        }
        return list;
    }

    private String reformInput(String input) {
        input = input.toLowerCase().trim();
        input = input.replaceAll("\n", " ");
        input = input.replaceAll("\\s{2,}", " ").trim();
        return input;
    }

//    public static void main(String[] args) {
//        final String path = "res\\test.txt";
//        SearchEngine searchEngine = new SearchEngine(3);
//        searchEngine.indexWebPage(path);
////        searchEngine.indexDirectory("res");
//
//        List<ISearchResult> res = searchEngine.searchByMultipleWordWithRanking("Hamza fucking");
//        int x = 0;
//    }
}
