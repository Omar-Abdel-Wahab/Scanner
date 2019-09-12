package scannerv14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Scannerv14 {

    public enum state {
        START, INNUM, INID, INASSIGN, OTHER, INCOMMENT, DONE
    };

    public enum tokenType {
        ReservedWord, SpecialSymbol, Identifier, Number, Undefined, Empty, LeftBracket, RightBracket, Comment
    };

    static private ArrayList<tokenType> tts = new ArrayList();
    static private ArrayList<String> ss = new ArrayList();
    static private int index = 0;
    static private char[] inputChars;
    static private state lastState;

    public static boolean isSpecialSymbol(char ch) {
        char[] cs = {'+', '-', '*', '/', '=', '<', '(', ')', ';'};
        for (int i = 0; i < cs.length; i++) {
            if (ch == cs[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReservedWord(String s) {
        String[] ss = {"if", "then", "else", "end", "repeat", "until", "read", "write"};
        for (int i = 0; i < ss.length; i++) {
            if (s.equals(ss[i])) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String fileInput = "";
        File input = new File("Input File.txt");

        try {
            FileReader fr = new FileReader(input);
            BufferedReader br = new BufferedReader(fr);
            while (br.ready()) {
                fileInput += br.readLine();
            }
            br.close();
            fr.close();

        } catch (FileNotFoundException ex) {
            System.out.println("FNF Exception");
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }

        inputChars = fileInput.toCharArray();
        tokenType tt;
        while (index < inputChars.length) {
            tt = getToken();
            if (tt != tokenType.Empty) {
                tts.add(tt);
            }
        }
        saveOutput();
        setTokensArrayList();
        saveOutput();
    }

    public static String[] outputString() {
        String[] os = new String[tts.size()];
        for (int i = 0; i < tts.size() - 1; i++) {
            if (tts.get(i) == tokenType.Comment) {
                os[i] = ss.get(i) + "\n";
            } else {
                os[i] = ss.get(i) + " : " + tts.get(i) + "\n";
            }
        }
        int length = tts.size();
        os[length - 1] = ss.get(length - 1) + " : " + tts.get(length - 1) + "\n";
        return os;
    }

    public static void saveOutput() {
        File f = new File("Output File.txt");
        String[] os = outputString();
        try {
            FileWriter fw = new FileWriter(f, false);
            PrintWriter pw = new PrintWriter(fw, true);
            for (int i = 0; i < os.length; i++) {
                pw.println(os[i]);
            }
            pw.close();
            fw.close();
        } catch (IOException ex) {
            System.out.println("IO Exception");
        }
    }
    
    public static void setTokensArrayList(){
        int size = tts.size();
        for(int i = 0; i < size; i++){
                if(tts.get(i) == tokenType.Comment){
                    tts.remove(i);
                    ss.remove(i);
                    size--;
                }
        }
    }

    public static tokenType getToken() {
        char currentChar;
        String currentString = "";
        tokenType currentTokenType = tokenType.Empty;
        state currentState = state.START;
        while (index < inputChars.length && currentState != state.DONE) {
            currentChar = inputChars[index];
            if (lastState != state.INCOMMENT) {
                if (Character.isWhitespace(currentChar)) {
                    currentState = state.START;
                } else if (Character.isLetter(currentChar)) {
                    currentState = state.INID;
                } else if (Character.isDigit(currentChar)) {
                    currentState = state.INNUM;
                } else if (isSpecialSymbol(currentChar)) {
                    currentState = state.OTHER;
                } else if (currentChar == ':') {
                    currentState = state.INASSIGN;
                } else if (currentChar == '{') {
                    currentState = state.INCOMMENT;
                    currentString += currentChar;
                    ss.add(currentString);
                    currentTokenType = tokenType.LeftBracket;
                    lastState = currentState;
                    break;
                }
            }
            if (currentState == state.START && lastState != state.INCOMMENT) {
                index++;
                if (index == inputChars.length) {
                    break;
                }
                currentChar = inputChars[index];
            }
            if (currentState == state.INID && lastState != state.INCOMMENT) {
                currentTokenType = tokenType.Identifier;
                do {
                    currentString += currentChar;
                    boolean b = isReservedWord(currentString);
                    if (b) {
                        index++;
                        break;
                    }
                    index++;
                    if (index == inputChars.length) {
                        break;
                    }
                    currentChar = inputChars[index];
                } while (Character.isLetter(currentChar));
                if (Character.isWhitespace(currentChar)) {
                    index++;
                }
//                if (Character.isDigit(currentChar) || isSpecialSymbol(currentChar)) {
//                }
                if (isReservedWord(currentString)) {
                    currentTokenType = tokenType.ReservedWord;
                }
                currentState = state.DONE;
                ss.add(currentString);
            }

            if (currentState == state.INNUM && lastState != state.INCOMMENT) {
                currentTokenType = tokenType.Number;
                do {
                    currentString += currentChar;
                    index++;
                    if (index == inputChars.length) {
                        break;
                    }
                    currentChar = inputChars[index];
                } while (Character.isDigit(currentChar));
                if (Character.isWhitespace(currentChar)) {
                    index++;
                }
//                if (Character.isLetter(currentChar) || isSpecialSymbol(currentChar)) {
//                }
                currentState = state.DONE;
                ss.add(currentString);
            }
            if (currentState == state.INASSIGN && lastState != state.INCOMMENT) {
                currentTokenType = tokenType.SpecialSymbol;
                currentString += currentChar;
                index++;
                if (index == inputChars.length) {
                    break;
                }
                currentChar = inputChars[index];
                if (currentChar == '=') {
                    currentString += currentChar;
                    index++;
                } else {
                    currentTokenType = tokenType.Undefined;
                }
                ss.add(currentString);
                currentState = state.DONE;
            }
            if (currentState == state.OTHER && lastState != state.INCOMMENT) {
                currentTokenType = tokenType.SpecialSymbol;
                currentString += currentChar;
                index++;

                ss.add(currentString);
                currentState = state.DONE;
            }

            if (lastState == state.INCOMMENT && currentChar != '}') {
                index++;
                currentChar = inputChars[index];
                while (currentChar != '}') {
                    currentString += currentChar;
                    index++;
                    if (index == inputChars.length) {
                        break;
                    }
                    currentChar = inputChars[index];
                }
                ss.add(currentString);
                currentTokenType = tokenType.Comment;
                break;
//                index++;
//                currentState = state.START;
            }
            if (lastState == state.INCOMMENT && currentChar == '}') {
                currentString += currentChar;
                ss.add(currentString);
                currentTokenType = tokenType.RightBracket;
                index++;
                currentState = state.START;
                lastState = currentState;
                break;
            }
        }
        return currentTokenType;
    }
}
