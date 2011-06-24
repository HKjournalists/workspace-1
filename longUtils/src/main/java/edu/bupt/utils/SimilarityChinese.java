/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.utils;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.wltea.analyzer.lucene.IKTokenizer;

/**
 *中文字符串的相似度比较
 *
 * @author oulong
 */
public class SimilarityChinese {

    private static final Logger logger = Logger.getLogger(SimilarityChinese.class);

    //分词获得单词 比较不同 取并集个数为分母 交集个数为分子 计算相似度
    public static double Comparability(String first, String second) {
        double d = 0.0;
        Set<String> set = new HashSet<String>();
        Set<String> set1 = Segements(first);
        Set<String> set2 = Segements(second);
        set.addAll(set1);
        for (String s : set2) {
            if (!set.contains(s)) {
                set.add(s);
            }
        }
        d = (set1.size() + set2.size() - set.size()) / (double) set.size();

        //保留6位小数 利于比较
        DecimalFormat df = new DecimalFormat("###.000000");
        d = Double.parseDouble(df.format(d));

        return d;
    }

    private static Set<String> Segements(String chineseString) {
        Set<String> set = new HashSet<String>();

        IKTokenizer tokenizer = new IKTokenizer(new StringReader(chineseString), false);
        try {
            while (tokenizer.incrementToken()) {
                TermAttribute termAtt = tokenizer.getAttribute(TermAttribute.class);
                set.add(termAtt.term());
            }
        } catch (IOException e) {
            logger.error("IO erro.", e);
        }

        return set;
    }
}
