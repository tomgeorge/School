/* Bantam Java Compiler and Language Toolset.
   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.
   The Bantam Java toolset is distributed under the following 
   conditions:
     You may make copies of the toolset for your own use and 
     modify those copies.
     All copies of the toolset must retain the author names and 
     copyright notice.
     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.
   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
/*
/* code below is copied to the file containing the lexer */
package lexer;
import parser.TokenIds;
/* import Symbol class, which represents the symbols that are passed
   from the lexer to the parser.  Each symbol consists of an ID 
   and a token value, which is defined in Token.java */
import java_cup.runtime.Symbol;


public class Lexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

    /* code below is copied to the class containing the lexer */
    /** maximum string size allowed */
    private final int MAX_STRING_SIZE = 5000;
    /** boolean indicating whether debugging is enabled */
    private boolean debug = false;
    /** boolean indicating whether we're lexing multiple files or a single file */
    private boolean multipleFiles = false;
    /** array that holds the names of each file we're lexing 
      * (used only when multipleFiles is true)
      * */
    private String[] filenames;
    /** array that holds the reader for each file we're lexing 
      * (used only when multipleFiles is true)
      * */
    private java.io.BufferedReader[] fileReaders;
    /** current file number used to index filenames and fileReaders
      * (used only when multipleFiles is true)
      * */
    private int fileCnt = 0;
    /** Lexer constructor - defined in JLex specification file
      * Needed to handle lexing multiple files
      * @param filenames list of filename strings
      * @param debug boolean indicating whether debugging is enabled
      * */
    public Lexer(String[] filenames, boolean debug) {
	// call private constructor, which does some initialization
	this();
	this.debug = debug;
	// set the multipleFiles flag to true (provides compatibility
	// with the single file constructors)
	multipleFiles = true;
	// initialize filenames field to parameter filenames
	// used later for finding the name of the current file
	this.filenames = filenames;
	// check that there is at least one specified filename
	if (filenames.length == 0)
	    throw new RuntimeException("Must specify at least one filename to scan");
	// must initialize readers for each file (BufferedReader)
	fileReaders = new java.io.BufferedReader[filenames.length];
	for (int i = 0; i < filenames.length; i++) {
	    // try...catch checks if file is found
	    try {
		// create the ith file reader
		fileReaders[i] = new java.io.BufferedReader(new java.io.FileReader(filenames[i]));
	    }
	    catch(java.io.FileNotFoundException e) {
		// if file not found then report an error and exit
		System.err.println("Error: file '" + filenames[i] + "' not found");
		System.exit(1);
	    }
	}
	// set yy_reader (a JLex variable) to the first file reader
	yy_reader = fileReaders[0];
	// set yyline to 1 (as opposed to 0)
	yyline = 1;
    }
    /** holds the current string constant
      * note: we use StringBuffer so that appending does not require constructing a new object 
      * */
    private StringBuffer currStringConst;
    /** getter method for accessing the current line number
      * @return current line number
      * */
    public int getCurrLineNum() {
	return yyline;
    }
    /** getter method for accessing the current file name
      * @return current filename string
      * */
    public String getCurrFilename() {
	return filenames[fileCnt];
    }
    /** print tokens - used primarily for debugging the lexer 
      * */
    public void printTokens() throws java.io.IOException {
	// prevFileCnt is used to determine when the filename has changed
	// every time an EOF is encountered fileCnt is incremented
	// by testing fileCnt with prevFileCnt, we can determine when the
	// filename has changed and print the filename along with the tokens
	int prevFileCnt = -1;
	// try...catch needed since next_token() can throw an IOException
	try {
	    // iterate through all tokens
	    while (true) {
		// get the next token
		Symbol symbol = next_token();
		// check if file has changed
		if (prevFileCnt != fileCnt) {
		    // if it has then print out the new filename
		    System.out.println("# " + filenames[fileCnt]);
		    // update prevFileCnt
		    prevFileCnt = fileCnt;
		}
		// print out the token
		System.out.println((Token)symbol.value);
		// if we've reached the EOF (EOF only returned for the last
		// file) then we break out of loop
		if (symbol.sym == TokenIds.EOF)
		    break;
	    }
	}
	catch (java.io.IOException e) {
	    // if an IOException occurs then print error and exit
	    System.err.println("Unexpected IO exception while scanning.");
	    throw e;
	}
    }

String stringContents="";
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Lexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Lexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Lexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

    // set yyline to 1 (as opposed to 0)
    yyline = 1;
	}

	private boolean yy_eof_done = false;
	private final int STRING = 3;
	private final int ESCAPE = 4;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int SLASHCOMMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		57,
		46,
		48,
		63
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"46:9,41,36,46,40,43,46:18,44,27,42,46:2,26,7,46,23,24,34,30,13,25,15,14,37:" +
"10,46,33,22,8,19,46:2,38:26,46,45,46:2,39,46,3,9,1,18,11,21,38,35,20,38:2,2" +
",38,12,10,38:2,31,4,17,32,38,28,16,38:2,5,29,6,46:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,113,
"0,1,2,1:2,3,4,1,5,1,6,7,1:4,8,1:3,9,10,1:6,11,1:3,12,11:11,1:2,13,1,14,1:8," +
"15,16,17,18,19,20,21,22,1,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,3" +
"9,40,41,42,43,44,45,46,47,11,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,1" +
"1,63,64,65,66,67")[0];

	private int yy_nxt[][] = unpackFromString(68,47,
"1,2,107:3,3,4,5,6,109,107,88,77,7,8,9,107,89,107,10,58,110,11,12,13,14,15,1" +
"6,111,59,17,112,107,18,19,107,20,21,107,65:2,20,22,-1,20,65:2,-1:48,107,90," +
"107:2,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107" +
",91,-1:14,23,-1:47,24,-1:52,25,-1:19,26,-1:20,27,-1:46,29,-1:46,30,-1:74,20" +
",-1:4,20,-1:2,20,-1:3,32:4,-1:4,32:4,-1:3,32:3,-1,32:2,-1:6,32,-1:2,32:2,-1" +
":2,32,-1,21,32:2,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,10" +
"7:2,-1:2,107,-1,91,107,91,-1:8,32:4,-1:4,32:4,-1:3,32:3,-1,32:2,-1:6,32,-1:" +
"2,32:2,-1:2,32,-1,32:3,-1:7,1,61:35,47,61:10,1,62:35,49,62:3,65:2,50,62:2,6" +
"7,62,1,44:33,60,44:12,-1,107:4,-1:4,107:3,66,-1:3,107:3,-1,107,28,-1:6,107," +
"-1:2,107:2,-1:2,107,-1,91,107,91,-1:36,31,-1:31,45,-1:33,61:35,-1,61:10,-1," +
"62:35,-1,62:3,-1:3,62:2,-1,62,1,65:35,-1,65:6,-1,65:3,-1,107:4,-1:4,107:4,-" +
"1:3,107:3,-1,107:2,-1:6,33,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:3,99,-" +
"1:4,107:4,-1:3,107,34,107,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,9" +
"1,-1:8,51:11,52,51:4,53,51:3,54,51:20,55,51:2,56,51,-1,107:4,-1:4,107:2,35," +
"107,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:" +
"4,-1:4,107:2,36,107,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91," +
"107,91,-1:8,107:3,37,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:" +
"2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2," +
"38,107,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:2,39,107,-1:3,107:3,-1,107" +
":2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:3,40,-1:3," +
"107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,10" +
"7:3,41,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,1" +
"07:3,42,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,1" +
"07,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107,43,-1:6,107,-1:2,107:2,-1:2,1" +
"07,-1,91,107,91,-1:8,107:4,-1:4,107:2,64,107,-1:3,107:3,-1,107:2,-1:6,107,-" +
"1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:3,68,-1:4,107:4,-1:3,107:3,-1,107:" +
"2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:" +
"3,-1,107:2,-1:6,107,-1:2,107,69,-1:2,107,-1,91,107,91,-1:8,107:3,70,-1:4,10" +
"7:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:" +
"4,-1:4,107,71,107:2,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91," +
"107,91,-1:8,107:3,69,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:" +
"2,107,-1,91,107,91,-1:8,107,72,107:2,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,10" +
"7,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:" +
"2,-1:6,107,-1:2,73,107,-1:2,107,-1,91,107,91,-1:8,107:2,74,107,-1:4,107:4,-" +
"1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:" +
"4,107:4,-1:3,107:2,75,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1" +
":8,107:4,-1:4,107,76,107:2,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107" +
",-1,91,107,91,-1:8,107,78,107:2,-1:4,107:4,-1:3,93,107:2,-1,107:2,-1:6,107," +
"-1:2,94,107,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:2" +
",-1:6,107,-1:2,79,107,-1:2,107,-1,91,107,91,-1:8,107:2,80,107,-1:4,107:4,-1" +
":3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4" +
",107,97,107:2,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91" +
",-1:8,107:4,-1:4,107:4,-1:3,107,98,107,-1,107:2,-1:6,107,-1:2,107:2,-1:2,10" +
"7,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,81,1" +
"07,-1:2,107,-1,91,107,91,-1:8,107,82,107:2,-1:4,107:4,-1:3,107:3,-1,107:2,-" +
"1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-" +
"1,83,107,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107,101,107:2,-1:4," +
"107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,10" +
"7:4,-1:4,107:2,102,107,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1," +
"91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107,103,107,-1,107:2,-1:6,107,-1:2,107" +
":2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107" +
",-1:2,107,84,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:2,85,107,-1:3,107:3," +
"-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:3,86" +
",-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:2,1" +
"04,107,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,10" +
"7,91,-1:8,107:4,-1:4,107:3,105,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2" +
",107,-1,91,107,91,-1:8,106,107:3,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1" +
":2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:2,87,107,-1:3,107:3,-1,1" +
"07:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,1" +
"07,100,107,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:8,107:4,-1" +
":4,107,92,107:2,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107," +
"91,-1:8,107:2,95,107,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2,107:2,-1:" +
"2,107,-1,91,107,91,-1:8,107:4,-1:4,107:4,-1:3,107:3,-1,107:2,-1:6,107,-1:2," +
"107:2,-1:2,96,-1,91,107,91,-1:8,107:4,-1:4,107:2,108,107,-1:3,107:3,-1,107:" +
"2,-1:6,107,-1:2,107:2,-1:2,107,-1,91,107,91,-1:7");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

    /* code below is executed when the end-of-file is reached */
    switch(yy_lexical_state) {
    case YYINITIAL:
	// if in YYINITIAL when EOF occurs then no error
	break;
    case COMMENT:
    {
        yybegin(YYINITIAL);
        return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Reached end of file while in comment", yyline));
    }
    case STRING:
    {
        yybegin(YYINITIAL);
        return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Reached end of file while in string", yyline));
    }
    // if defining other states then might want to add other cases here...
    }
    // if we reach here then we should either start lexing the next
    // file (if there are more files to lex) or return EOF (if we're
    // at the file)
    if (multipleFiles && fileCnt < fileReaders.length - 1) {
	// more files to lex so update yy_reader and yyline and then continue
	yy_reader = fileReaders[++fileCnt];
	yyline = 1;
	continue;
    }
    // if we reach here, then we're at the last file so we return EOF
    // to parser
    return new Symbol(TokenIds.EOF, new Token("EOF", yyline));
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -3:
						break;
					case 3:
						{return new Symbol(TokenIds.LBRACE, new Token("{", yyline));}
					case -4:
						break;
					case 4:
						{return new Symbol(TokenIds.RBRACE, new Token("}", yyline));}
					case -5:
						break;
					case 5:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unmatched Lexeme "+yytext(), yyline));}
					case -6:
						break;
					case 6:
						{return new Symbol(TokenIds.ASSIGN, new Token("=", yyline));}
					case -7:
						break;
					case 7:
						{return new Symbol(TokenIds.COMMA, new Token(",", yyline));}
					case -8:
						break;
					case 8:
						{return new Symbol(TokenIds.DIVIDE, new Token("/", yyline));}
					case -9:
						break;
					case 9:
						{return new Symbol(TokenIds.DOT, new Token(".", yyline));}
					case -10:
						break;
					case 10:
						{return new Symbol(TokenIds.GT, new Token(">", yyline));}
					case -11:
						break;
					case 11:
						{return new Symbol(TokenIds.LT, new Token("<", yyline));}
					case -12:
						break;
					case 12:
						{return new Symbol(TokenIds.LPAREN, new Token("(", yyline));}
					case -13:
						break;
					case 13:
						{return new Symbol(TokenIds.RPAREN, new Token(")", yyline));}
					case -14:
						break;
					case 14:
						{return new Symbol(TokenIds.MINUS, new Token("-", yyline));}
					case -15:
						break;
					case 15:
						{return new Symbol(TokenIds.MODULUS, new Token("%", yyline));}
					case -16:
						break;
					case 16:
						{return new Symbol(TokenIds.NOT, new Token("!", yyline));}
					case -17:
						break;
					case 17:
						{return new Symbol(TokenIds.PLUS, new Token("+", yyline));}
					case -18:
						break;
					case 18:
						{return new Symbol(TokenIds.SEMI, new Token(";", yyline));}
					case -19:
						break;
					case 19:
						{return new Symbol(TokenIds.TIMES, new Token("*", yyline));}
					case -20:
						break;
					case 20:
						{}
					case -21:
						break;
					case 21:
						{  
                        Integer i;
                        try{
                            i = new Integer(yytext());
                            int theInteger = i.intValue();
                            }
                            catch(NumberFormatException n){
                                return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Int "+yytext()+ "is too large", yyline));
                            }   
                                return new Symbol(TokenIds.INT_CONST, new Token("INT_CONST",yytext(), yyline));
                    }
					case -22:
						break;
					case 22:
						{
		yybegin(STRING);
		stringContents = "";
		}
					case -23:
						break;
					case 23:
						{return new Symbol(TokenIds.AND, new Token("&&", yyline));}
					case -24:
						break;
					case 24:
						{return new Symbol(TokenIds.EQ, new Token("==", yyline));}
					case -25:
						break;
					case 25:
						{yybegin(SLASHCOMMENT);}
					case -26:
						break;
					case 26:
						{yybegin(COMMENT);}
					case -27:
						break;
					case 27:
						{return new Symbol(TokenIds.GEQ, new Token(">=", yyline));}
					case -28:
						break;
					case 28:
						{return new Symbol(TokenIds.IF, new Token("IF", yyline));}
					case -29:
						break;
					case 29:
						{return new Symbol(TokenIds.LEQ, new Token("<=", yyline));}
					case -30:
						break;
					case 30:
						{return new Symbol(TokenIds.NE, new Token("!=", yyline));}
					case -31:
						break;
					case 31:
						{return new Symbol(TokenIds.OR, new Token("||", yyline));}
					case -32:
						break;
					case 32:
						{
						return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Identifiers may not start with digits", yyline));
						}
					case -33:
						break;
					case 33:
						{return new Symbol(TokenIds.NEW, new Token("NEW", yyline));}
					case -34:
						break;
					case 34:
						{return new Symbol(TokenIds.ID, new Token("ID",yytext(), yyline));}
					case -35:
						break;
					case 35:
						{return new Symbol(TokenIds.ELSE, new Token("ELSE", yyline));}
					case -36:
						break;
					case 36:
						{return new Symbol(TokenIds.BOOLEAN_CONST, new Token("BOOLEAN_CONST", yytext(), yyline));}
					case -37:
						break;
					case 37:
						{ return new Symbol(TokenIds.CLASS, 
                                  new Token("CLASS", yyline)); }
					case -38:
						break;
					case 38:
						{return new Symbol(TokenIds.error, new Token("ERROR", yyline));}
					case -39:
						break;
					case 39:
						{return new Symbol(TokenIds.WHILE, new Token("WHILE", yyline));}
					case -40:
						break;
					case 40:
						{return new Symbol(TokenIds.RETURN, new Token("RETURN", yyline));}
					case -41:
						break;
					case 41:
						{return new Symbol(TokenIds.ID, new Token("ID", yytext(), yyline));}
					case -42:
						break;
					case 42:
						{return new Symbol(TokenIds.EXTENDS, new Token("EXTENDS", yyline));}
					case -43:
						break;
					case 43:
						{return new Symbol(TokenIds.INSTANCEOF, new Token("INSTANCEOF", yyline));}
					case -44:
						break;
					case 44:
						{}
					case -45:
						break;
					case 45:
						{ yybegin(YYINITIAL);}
					case -46:
						break;
					case 46:
						{ }
					case -47:
						break;
					case 47:
						{yybegin(YYINITIAL);}
					case -48:
						break;
					case 48:
						{
		stringContents += yytext();
		}
					case -49:
						break;
					case 49:
						{
		yybegin(YYINITIAL);
		return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unterminated String", yyline));
		}
					case -50:
						break;
					case 50:
						{
	yybegin(YYINITIAL);
	return new Symbol(TokenIds.STRING_CONST, new Token("STRING_CONST", stringContents, yyline));
	}
					case -51:
						break;
					case 51:
						{
		return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_EROR", "Invalid escape: "+ yytext(), yyline));
		}
					case -52:
						break;
					case 52:
						{
		stringContents += "\n";
		}
					case -53:
						break;
					case 53:
						{
		stringContents += "\t";
		}
					case -54:
						break;
					case 54:
						{
		stringContents += "\f";
		}
					case -55:
						break;
					case 55:
						{
		stringContents += "\"";
		}
					case -56:
						break;
					case 56:
						{
		stringContents += "\\";
		}
					case -57:
						break;
					case 58:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -58:
						break;
					case 59:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unmatched Lexeme "+yytext(), yyline));}
					case -59:
						break;
					case 60:
						{}
					case -60:
						break;
					case 61:
						{ }
					case -61:
						break;
					case 62:
						{
		stringContents += yytext();
		}
					case -62:
						break;
					case 64:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -63:
						break;
					case 65:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unmatched Lexeme "+yytext(), yyline));}
					case -64:
						break;
					case 66:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -65:
						break;
					case 67:
						{ return new Symbol(TokenIds.LEX_ERROR, new Token("LEX_ERROR", "Unmatched Lexeme "+yytext(), yyline));}
					case -66:
						break;
					case 68:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -67:
						break;
					case 69:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -68:
						break;
					case 70:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -69:
						break;
					case 71:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -70:
						break;
					case 72:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -71:
						break;
					case 73:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -72:
						break;
					case 74:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -73:
						break;
					case 75:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -74:
						break;
					case 76:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -75:
						break;
					case 77:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -76:
						break;
					case 78:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -77:
						break;
					case 79:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -78:
						break;
					case 80:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -79:
						break;
					case 81:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -80:
						break;
					case 82:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -81:
						break;
					case 83:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -82:
						break;
					case 84:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -83:
						break;
					case 85:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -84:
						break;
					case 86:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -85:
						break;
					case 87:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -86:
						break;
					case 88:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -87:
						break;
					case 89:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -88:
						break;
					case 90:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -89:
						break;
					case 91:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -90:
						break;
					case 92:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -91:
						break;
					case 93:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -92:
						break;
					case 94:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -93:
						break;
					case 95:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -94:
						break;
					case 96:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -95:
						break;
					case 97:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -96:
						break;
					case 98:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -97:
						break;
					case 99:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -98:
						break;
					case 100:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -99:
						break;
					case 101:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -100:
						break;
					case 102:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -101:
						break;
					case 103:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -102:
						break;
					case 104:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -103:
						break;
					case 105:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -104:
						break;
					case 106:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -105:
						break;
					case 107:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -106:
						break;
					case 108:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -107:
						break;
					case 109:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -108:
						break;
					case 110:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -109:
						break;
					case 111:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -110:
						break;
					case 112:
						{return new Symbol(TokenIds.ID, 
                                    new Token("ID",yytext(), yyline));}
					case -111:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
