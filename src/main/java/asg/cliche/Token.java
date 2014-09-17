/*
 * This file is part of the Cliche project, licensed under MIT License.
 * See LICENSE.txt file in root folder of Cliche sources.
 */

package asg.cliche;

import java.util.ArrayList;
import java.util.List;


/**
 * Token associates index of a token in the input line with the token itself,
 * in order to be able to provide helpful error indecation (see below :)
 * ------------------------------------------------^ Misspelled word! (Exactly how it should work).
 * <p/>
 * This class is immutable.
 * <p/>
 * Parsing procedural module is also within.
 */
public class Token {

    private int index;
    private String string;

    public Token(int index, String string) {
        super();
        this.index = index;
        this.string = string;
    }

    /**
     * State machine input string tokenizer.
     *
     * @param input String to be tokenized
     * @return List of tokens
     * @see asg.cliche.Shell.Token
     * @see asg.cliche.Shell.escapeString
     */
    /*package-private for tests*/
    static List<Token> tokenize(final String input) {
        List<Token> result = new ArrayList<Token>();
        if (input == null) {
            return result;
        }

        final int WHITESPACE = 0;
        final int WORD = 1;
        final int STRINGDQ = 2;
        final int STRINGSQ = 3;
        final int COMMENT = 4;

        int state = WHITESPACE;
        char ch; // character in hand
        int tokenIndex = -1;
        StringBuilder token = new StringBuilder("");

        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            switch (state) {
                case WHITESPACE:
                    if (Character.isWhitespace(ch)) {
                        // keep state
                    } else if (Character.isLetterOrDigit(ch) || ch == '_') {
                        state = WORD;
                        tokenIndex = i;
                        token.append(ch);
                    } else if (ch == '"') {
                        state = STRINGDQ;
                        tokenIndex = i;
                    } else if (ch == '\'') {
                        state = STRINGSQ;
                        tokenIndex = i;
                    } else if (ch == '#') {
                        state = COMMENT;
                    } else {
                        state = WORD;
                        tokenIndex = i;
                        token.append(ch);
                    }
                    break;

                case WORD:
                    if (Character.isWhitespace(ch)) {
                        // submit token
                        result.add(new Token(tokenIndex, token.toString()));
                        token.setLength(0);
                        state = WHITESPACE;
                    } else if (Character.isLetterOrDigit(ch) || ch == '_') {
                        token.append(ch); // and keep state
                    } else if (ch == '"') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '"') {
                            // Yes, it's somewhat wrong in terms of statemachine, but it's the
                            // simplest and crearest way.
                            token.append('"');
                            i++;
                            // and keep state.
                        } else {
                            state = STRINGDQ; // but don't append; a"b"c is the same as abc.
                        }
                    } else if (ch == '\'') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '\'') {
                            // Yes, it's somewhat wrong in terms of statemachine, but it's the
                            // simplest and crearest way.
                            token.append('\'');
                            i++;
                            // and keep state.
                        } else {
                            state = STRINGSQ; // but don't append; a"b"c is the same as abc.
                        }
                    } else if (ch == '#') {
                        // submit token
                        result.add(new Token(tokenIndex, token.toString()));
                        token.setLength(0);
                        state = COMMENT;
                    } else {
                        // for now we do allow special chars in words
                        token.append(ch);
                    }
                    break;

                case STRINGDQ:
                    if (ch == '"') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '"') {
                            token.append('"');
                            i++;
                            // and keep state
                        } else {
                            state = WORD;
                        }
                    } else {
                        token.append(ch);
                    }
                    break;

                case STRINGSQ:
                    if (ch == '\'') {
                        if (i < input.length() - 1 && input.charAt(i + 1) == '\'') {
                            token.append('\'');
                            i++;
                            // and keep state
                        } else {
                            state = WORD;
                        }
                    } else {
                        token.append(ch);
                    }
                    break;

                case COMMENT:
                    // eat ch
                    break;

                default:
                    assert false : "Unknown state in Shell.tokenize() state machine";
                    break;
            }
        }

        if (state == WORD || state == STRINGDQ || state == STRINGSQ) {
            result.add(new Token(tokenIndex, token.toString()));
        }

        return result;
    }

    /**
     * Escape given string so that tokenize(escapeString(str)).get(0).getString === str.
     *
     * @param input String to be escaped
     * @return escaped string
     */
    public static String escapeString(String input) {
        StringBuilder escaped = new StringBuilder(input.length() + 10);
        escaped.append('"');
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '"') {
                escaped.append("\"\"");
            } else {
                escaped.append(input.charAt(i));
            }
        }
        escaped.append('"');
        return escaped.toString();
    }

    public final int getIndex() {
        return index;
    }

    public final String getString() {
        return string;
    }

    @Override
    public String toString() {
        return (string != null ? string : "(null)") + ":" + Integer.toString(index);
    }


    // *** Parser procmodule begins here ***

    @Override
    public boolean equals(Object obj) {
        // The contents generated by NetBeans.
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Token other = (Token) obj;
        if ((this.string == null) ? (other.string != null) : !this.string.equals(other.string)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.string != null ? this.string.hashCode() : 0);
        return hash;
    }
}
