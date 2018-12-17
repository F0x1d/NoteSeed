package com.f0x1d.notes.help.utils;

import java.util.regex.Pattern;

public class f0x1ds_coder {

    public String encode(String text){
        text = text.toLowerCase();

        System.out.println("");
        text = text.replace(" ", "-");
        if (text.contains("0")) text = text.replace("0", "_!_");
        if (text.contains("1")) text = text.replace("1", "_@_");
        if (text.contains("2")) text = text.replace("2", "_#_");
        if (text.contains("3")) text = text.replace("3", "_$_");
        if (text.contains("4")) text = text.replace("4", "_%_");
        if (text.contains("5")) text = text.replace("5", "_^_");
        if (text.contains("6")) text = text.replace("6", "_&_");
        if (text.contains("7")) text = text.replace("7", "_*_");
        if (text.contains("8")) text = text.replace("8", "_(_");
        if (text.contains("9")) text = text.replace("9", "_)_");
        if (text.contains("a")) text = text.replace("a", "_0_");
        if (text.contains("b")) text = text.replace("b", "_1_");
        if (text.contains("c")) text = text.replace("c", "_2_");
        if (text.contains("d")) text = text.replace("d", "_3_");
        if (text.contains("e")) text = text.replace("e", "_4_");
        if (text.contains("f")) text = text.replace("f", "_5_");
        if (text.contains("g")) text = text.replace("g", "_6_");
        if (text.contains("h")) text = text.replace("h", "_7_");
        if (text.contains("i")) text = text.replace("i", "_8_");
        if (text.contains("j")) text = text.replace("j", "_9_");
        if (text.contains("k")) text = text.replace("k", "_10_");
        if (text.contains("l")) text = text.replace("l", "_11_");
        if (text.contains("m")) text = text.replace("m", "_12_");
        if (text.contains("n")) text = text.replace("n", "_13_");
        if (text.contains("o")) text = text.replace("o", "_14_");
        if (text.contains("p")) text = text.replace("p", "_15_");
        if (text.contains("q")) text = text.replace("q", "_16_");
        if (text.contains("r")) text = text.replace("r", "_17_");
        if (text.contains("s")) text = text.replace("s", "_18_");
        if (text.contains("t")) text = text.replace("t", "_19_");
        if (text.contains("u")) text = text.replace("u", "_20_");
        if (text.contains("v")) text = text.replace("v", "_21_");
        if (text.contains("w")) text = text.replace("w", "_22_");
        if (text.contains("x")) text = text.replace("x", "_23_");
        if (text.contains("y")) text = text.replace("y", "_24_");
        if (text.contains("z")) text = text.replace("z", "_25_");
        System.out.println("");

        return text;
    }

    public String decode(String text){
        text = text.toLowerCase();

        System.out.println("");
        text = text.replace("-", " ");
        text = text.replace("_0_", "a");
        text = text.replace("_1_", "b");
        text = text.replace("_2_", "c");
        text = text.replace("_3_", "d");
        text = text.replace("_4_", "e");
        text = text.replace("_5_", "f");
        text = text.replace("_6_", "g");
        text = text.replace("_7_", "h");
        text = text.replace("_8_", "i");
        text = text.replace("_9_", "j");
        text = text.replace("_10_", "k");
        text = text.replace("_11_", "l");
        text = text.replace("_12_", "m");
        text = text.replace("_13_", "n");
        text = text.replace("_14_", "o");
        text = text.replace("_15_", "p");
        text = text.replace("_16_", "q");
        text = text.replace("_17_", "r");
        text = text.replace("_18_", "s");
        text = text.replace("_19_", "t");
        text = text.replace("_20_", "u");
        text = text.replace("_21_", "v");
        text = text.replace("_22_", "w");
        text = text.replace("_23_", "x");
        text = text.replace("_24_", "y");
        text = text.replace("_25_", "z");
        text = text.replace("_!_", "0");
        text = text.replace("_@_", "1");
        text = text.replace("_#_", "2");
        text = text.replace("_$_", "3");
        text = text.replace("_%_", "4");
        text = text.replace("_^_", "5");
        text = text.replace("_&_", "6");
        text = text.replace("_*_", "7");
        text = text.replace("_(_", "8");
        text = text.replace("_)_", "9");
        System.out.println("");
        return text;
    }
}