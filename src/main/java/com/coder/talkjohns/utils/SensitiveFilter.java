package com.coder.talkjohns.utils;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // replacing words
    private static final String REPLACEMENT = "***";

    // root node
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null){
                // add to TRIE
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("Failed to load the sensitive-words.txt file." + e.getMessage());
        }
    }

    // add 1 keyword to the trie
    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                // create a new node
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // tempNode points to the subNode, continue loop
            tempNode = subNode;

            // set end flag
            if(i == keyword.length() - 1){
                tempNode.setIsKeywordEnd(true);
            }
        }
    }

    /**
     * filter sensitive words
     * @param text
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // pointer 1
        TrieNode tempNode = rootNode;
        // pointer 2
        int begin = 0;
        // pointer 3
        int position = 0;
        // result text
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            if(position < text.length()) {
                Character c = text.charAt(position);

                // ignore symbols
                if (isSymbol(c)) {
                    if (tempNode == rootNode) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // current string is not sensitive word
                    sb.append(text.charAt(begin));
                    // continue
                    position = ++begin;
                    tempNode = rootNode;
                } else if (tempNode.getIsKeywordEnd()) { // sensitive word
                    sb.append(REPLACEMENT);
                    begin = ++position;
                    tempNode = rootNode;
                } else {
                    position++;
                }
            } else{ // position out of range
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }
        }
        return sb.toString();
    }

    private boolean isSymbol(Character c){
//        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
        return !CharUtils.isAsciiAlphanumeric(c);
    }
    private class TrieNode{

        // sensitive word ending flag
        private boolean isKeywordEnd = false;

        // children node
        private Map<Character, TrieNode> subNodes= new HashMap<>();

        public boolean getIsKeywordEnd() {
            return isKeywordEnd;
        }

        public void setIsKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // add children node
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}





















