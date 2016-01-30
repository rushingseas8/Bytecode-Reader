import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

public class Main {
    private static String path;
    private static File encodedFile;
    private static File decodedFile;
    private static InputStreamReader reader;

    private static ArrayList<Integer> contents; //Holds the contents of the file, one char at a time.
    private static ArrayList<Byte> contentsHex; //Holds the contents of the file, in a number from 0-255; One byte.
    private static String content; //Holds a String representation of the file. Fixes encoding issues.

    private static String[] constantPool; //A list of all of the constants defined in a given class. Defined in interpret().
    private static String[] entries; //Similar to constantPool, holds an augmented list of entries for more detail.

    /**
     * Alternative main method of the program; currently not finished; use the normal main method for normal use.
     * @param p: The name of the class file (with or without .class at the end) to use
     */
    public static void main(String p) {
        //if (!p.contains(".class")) { p+=(".class"); }
        //path = p;
    }

    /**
     * Starts the program.
     * Text-based program with prompts as needed.
     */
    public static void main() {
        System.out.println("Class file decoder version 2014-01-28");
        System.out.println("*************************************");

        //Keeps asking until a valid file has been provided.
        while(true) {

            System.out.println("Please enter in the location of your file of choice, or");
            System.out.println("   the name of the file if it's in this directory.");
            System.out.print  ("?: ");

            Scanner scanner = new Scanner(System.in);
            String tempPath = scanner.nextLine();

            System.out.println();

            tempPath = tempPath.trim(); //Trims whitespace from input

            //Tests for ".class" in the string, and fixes it.
            if (tempPath.length() < 6 || !tempPath.substring(tempPath.length() - 6).equals(".class")) {
                System.out.println("WARNING: The name provided does not have a .class ending.");

                if (tempPath.contains(".")) {
                    System.out.println("It looks like you mistyped it-.");
                    tempPath = tempPath.substring(0, tempPath.indexOf(".")) + ".class";
                } else {
                    tempPath+=".class";
                }
                System.out.println("    >Fixed to " + tempPath + ".");
            }

            path = tempPath; //Set the actual path as the fixed path.
            System.out.println();

            try {
                //Note that this will throw a FileNotFoundException if the File does not exist.
                reader = new InputStreamReader(new FileInputStream(path), "ISO-8859-1"); //And then open a writer to the file
                System.out.println("Successfully opened the file reader.");

                encodedFile = new File(path); //Try to open the file with the given name
                System.out.println("Successfully added the file to memory: " + path + ".\n");    

                break; //When we have a file with no errors, then break out of the infinite loop.
            }
            catch (FileNotFoundException e) {
                System.out.println("ERROR: Could not find the given file. Check to make sure");
                System.out.println("    that it is spelled correctly and that the directory");
                System.out.println("    is correct.");
                System.out.println();
            }
            catch (UnsupportedEncodingException u) {
                System.out.println("ERROR: Could not find the given character encoding. Check");
                System.out.println("    that it is spelled correctly.");
                System.out.println();                
            }
        }

        //Sets up the file in memory, readying it to be translated.
        System.out.print("Parsing through file... ");
        parse();
        System.out.println("Done.\n");

        //Resets the reader so that it can be reused.
        try {reader = new InputStreamReader(new FileInputStream(path), "ISO-8859-1");}
        catch(FileNotFoundException f) {System.out.println("ERROR: The file somehow was deleted while the program was running?");}
        catch(UnsupportedEncodingException u) {System.out.println("ERROR: The character encoding was deleted while the program was running?");}

        //Sets up the bytecode.
        System.out.print("Converting to bytecode... ");
        parseHex();
        System.out.println("Done.\n");

        //Ask for translations until the user gets tired of the program.
        while(true) {
            System.out.println("What translation would you like?");
            System.out.println("    1. Raw characters (numbers in () are ID's to avoid ASCII control chars)");
            System.out.println("    2. Raw bytecode");
            System.out.println("    3. Interpretation of bytecode (Currently in progress)");
            System.out.println("    0. (End program)");

            Scanner scanner = new Scanner(System.in);
            int input = -1;

            //Handles user errors gracefully.
            try { input = scanner.nextInt(); }
            catch (java.util.InputMismatchException i) {
                System.out.print("That is not a number; "); }

            //Handles proper user inputs gracefully.
            if      (input == 1) { convertToRaw(); }
            else if (input == 2) { convertToHex(); }
            else if (input == 3) { interpret(); }
            else if (input == 0) { break; }
            else    System.out.println("Invalid input.");
        }
    }

    /**
     * Parses through the input file, character by character, and adds it to the ArrayList of contents.
     */
    private static void parse() {
        contents = new ArrayList<>();
        content = "";

        try {   
            int r;
            while ((r = reader.read()) != -1) {
                char ch = (char) r;
                contents.add(r);
                content+=ch;
            }                     
        }
        catch (Exception e) {
            System.out.println("Error: A fatal error occured in parse() when reading the contents");
            System.out.println("    of the file " + path + ".");
            e.printStackTrace(System.out);
        }
    }

    /**
     * Similar to method parse(), this goes through the file character by character and adds hexadecimal equivilant bytes to
     * the Arraylist of byte contents.
     */
    private static void parseHex() {
        contentsHex = new ArrayList<>();

        try {
            byte first, second;
            int nextChar;
            while ((nextChar = reader.read()) != -1) {
                second = (byte)(nextChar - 128); //Reads the raw character and conforms it to -128 to 127 (byte)
                contentsHex.add(second); //Then adds the byte to the list of contents
            }
        } catch (Exception e) {
            System.out.println("Error: A fatal error occured in parseHex() when reading the contents");
            System.out.println("    of the file " + path + ".");
            e.printStackTrace(System.out);            
        }
    }

    /**
     * Converts the parsed input back to raw characters. 
     * ASCII characters 0-32 and 127 are placed in parenthesis to avoid errant behavior.
     */
    private static void convertToRaw() {
        System.out.println();
        for (int i = 0; i < contents.size(); i++) { 
            if (contents.get(i) > 32 && contents.get(i) != 127)
                System.out.print(" " + ((char)((int)contents.get(i))) + "  "); //Normal case; double conversion because Integer->char isn't supported
            else 
                System.out.print("(" + contents.get(i) + ")" + " "); //If we have a control character, write in parenthesis

            if ((i+1) % 10 == 0) System.out.println(); //Adds one so that the line breaks are made properly.
        }

        System.out.println();
        System.out.println();
    }

    /**
     * Converts the parsed input to hexadecimal bytes.
     * Similar to method convertToRaw(), but works with contentsHex instead of contents.
     */
    private static void convertToHex() {
        System.out.println();

        for (int i = 0; i < contentsHex.size(); i++) {
            toHex(contentsHex.get(i) + 128); 
            if((i-1) % 10 == 0) {System.out.println();}
        }        

        System.out.println();
        System.out.println();
    }

    /**
     * Turns a number 0-255 to a hex value 00-FF.
     * Used as a helper method to save space.
     * Returns string that visually represents this hex number.
     */
    private static String toHex(int i) {
        int byte1; //The byte itself
        int b11, b12; //First and second number within the byte

        //Makes sure the number is within the bounds, just in case
        byte1 = i % 256;

        //Splits the bytes into two hex numbers
        b11 = byte1 / 16;
        b12 = byte1 % 16;        

        //Prints the byte as hex number
        printHex(b11);
        printHex(b12);       

        //Space
        System.out.print(" ");

        return ("" + b11 + b12);
    }

    /**
     * Prints a given number (0-15) as a hex number (0-F).
     * Used as a helper method to save space.
     */
    private static void printHex(int i) {
        switch(i) {
            case 10: System.out.print("A"); break;
            case 11: System.out.print("B"); break;
            case 12: System.out.print("C"); break;
            case 13: System.out.print("D"); break;
            case 14: System.out.print("E"); break;
            case 15: System.out.print("F"); break;     
            default: System.out.print( i );
        }
    }

    /**
     * Returns the hex string version of a decimal byte.
     */
    private static String stringHex(int i) {
        String toReturn = "";

        int num1 = i / 16, num2 = i % 16;

        switch(num1) {
            case 10: toReturn+="A"; break;
            case 11: toReturn+="B"; break;
            case 12: toReturn+="C"; break;
            case 13: toReturn+="D"; break;
            case 14: toReturn+="E"; break;
            case 15: toReturn+="F"; break;
            default: toReturn+=num1; break;
        }

        switch(num2) {
            case 10: toReturn+="A"; break;
            case 11: toReturn+="B"; break;
            case 12: toReturn+="C"; break;
            case 13: toReturn+="D"; break;
            case 14: toReturn+="E"; break;
            case 15: toReturn+="F"; break;
            default: toReturn+=num2; break;
        }        

        return toReturn;
    }

    /**
     * The most meaty method in this whole class; Takes the .class file that was found and converts it to
     * bytecode, and then interprets the information given.
     * 
     * 1 : Magic number
     * 2 : 
     * 3 :
     * 4 :
     * 5 :
     * 6 :
     * 7 :
     * 8 :
     * 9 :
     * 10:
     */
    private static void interpret() {
        System.out.println();

        int position = 0; //The position we're at in the array

        //PART 1: Reads the magic four bytes at the start.
        String magic = "";
        for (int i = 0; i < 4; i++) {
            int temp = readByte(position + i); magic+=stringHex(temp);
        }
        position+=4; //Position is now at 4
        if (magic.equals("CAFEBABE")) {System.out.println("Class file recognized!");}
        else { System.out.println("Didn't recognize the starting four bytes: " + magic); }

        //PART 2: Reads the version number.
        String minor = "", major = "";
        for (int i = 0; i < 4; i++) {
            int temp = readByte(position + i);
            if (i <= 1) minor+=stringHex(temp); else major+=stringHex(temp);
        }
        position+=4; //Position is now at 8

        //Handles major version
        switch(major) {
            case "0033": System.out.print("Major version: J2SE 7"); break;
            case "0032": System.out.print("Major version: J2SE 6.0"); break;
            case "0031": System.out.print("Major version: J2SE 5.0"); break;
            case "0030": System.out.print("Major version: JDK 1.4"); break;
            case "002F": System.out.print("Major version: JDK 1.3"); break;
            case "002E": System.out.print("Major version: JDK 1.2"); break;
            case "002D": System.out.print("Major version: JDK 1.1"); break;     
            default: System.out.print("Older or unrecognized version"); break;
        }

        //Minor version
        System.out.println(" / Minor version: " + minor );

        //PART 3: Constants
        int numConstants = 0;
        for (int i = 0; i < 2; i++) {
            int temp = readByte(position + i); 
            if (i == 0) numConstants+=256*temp; else numConstants+=temp;
        }
        position+=2; //Position is now at 10

        constantPool = new String[numConstants]; //Sets up the constant pool
        entries = new String[numConstants]; //And list of entries
        System.out.println("There appear to be " + (numConstants-1) + " constants.");
        System.out.println("Constant listing to follow:\n");

        int offsetConstant = 0; //How much we're going to be moved forward as a result of constants
        for (int i = 0; i < numConstants-1; i++) {
            //Tells us what kind of constant this is.
            int tag = readByte(position + offsetConstant);
            offsetConstant+=1;

            //Gives the type of constant, and the length of it
            String tagInfo = "";
            int length = 0;
            switch(tag) {
                case 1 : tagInfo = "Utf8 String";                length = 0;  break; //Note: Strings have variable length
                case 3 : tagInfo = "Integer";                    length = 4;  break;
                case 4 : tagInfo = "Float";                      length = 4;  break;
                case 5 : tagInfo = "Long";                       length = 8;  i++; constantPool[i] = "Long continued"; break; //Note: Longs and doubles take up two spaces in
                case 6 : tagInfo = "Double";                     length = 8;  i++; constantPool[i] = "Double continued";  break; //    The constant pool.
                case 7 : tagInfo = "Class reference";            length = 2;  break;
                case 8 : tagInfo = "String reference";           length = 2;  break;
                case 9 : tagInfo = "Field reference";            length = 4;  break;
                case 10: tagInfo = "Method reference";           length = 4;  break;
                case 11: tagInfo = "Interface method reference"; length = 4;  break;
                case 12: tagInfo = "Name & type descriptor";     length = 4;  break;
                default: tagInfo = "UNKOWN: " + tag;                        length = 0;  offsetConstant-=1; break; //Move back one if found this; must be out of bounds
            }

            //Assigns the length to the string
            if (tag==1) {
                length+=readByte(position+offsetConstant+0)*256; 
                length+=readByte(position+offsetConstant+1); 
                offsetConstant+=2;
            }

            //Gets the information from the constant
            String info = ""; //Put information into here
            int numInfo = 0; //Used for temporary number storage
            for (int j = 0; j < length; j++) {
                int temp = readByte(position + offsetConstant + j);
                switch(tag) {
                    case 1 : info+=(char)temp; break; //String; direct byte->char conversion
                    case 3 : info = "INTEGER"; break; //Todo: Two's compliment notation of these four bytes. //Integer
                    case 4 : info = "FLOAT";  break; //Float
                    case 5 : info = "LONG";  break; //Long
                    case 6 : info = "DOUBLE";  break; //Double
                    case 7 : if (j==0) numInfo+=(temp*256); else {numInfo+=temp; info="*" + numInfo + "*";} break; //Class ref; points to a String representation of a class name
                    case 8 : if (j==0) numInfo+=(temp*256); else {numInfo+=temp; info="*" + numInfo + "*";} break; //String ref
                    case 9 : 
                    if (j==0) numInfo+=(temp*256); else if(j==1) {numInfo+=temp; info+="Class *" + numInfo + "*" + " // "; numInfo = 0;} 
                    else if (j==2) numInfo+=(temp*256); else {numInfo+=temp; info+="Name&Type *" + numInfo + "*";}
                    break;          //Field reference

                    case 10: info = "METHOD REFERENCE";  break; //Method ref
                    case 11: info = "INTERFACE METHOD REFERENCE";  break; //Inter. meth. ref
                    case 12: info = "NAME AND TYPE DESCRIPTOR";  break; //Name & type desc
                    default:        //Unrecognized
                }
            }            

            constantPool[i] = info;            
            String entry = "#" + (i+1) + ": Constant type= " + tagInfo + " :: Length= " + length + " :: Contents= " + info;
            entries[i] = entry;

            offsetConstant+=length;
        }

        position+=offsetConstant; //Position is now 10 + the final value of offsetConstant.

        //Replaces temporary references [like *242*] with number references, and that reference itself.
        //This is needed because the above loop goes from top to bottom, and any references to furure information is not currently loaded.
        //Note that this isn't perfect; doesn't support multiple replacements in one line
        for (int i = 0; i < entries.length; i++) {

            if (entries[i] != null) {
                //Finds the first and next index of "*".
                int firstIndex = entries[i].indexOf("*");
                int nextIndex = entries[i].indexOf("*", firstIndex+1); //Starts looking at firstIndex

                //If there are two separate references to "*", then continue.
                if (firstIndex != -1 && nextIndex != -1) {
                    //The substring is from one asterisk to the next, inclusive.
                    String sub = entries[i].substring(firstIndex, nextIndex+1);
                    int fillIndex = -1; //What index to replace the substring with

                    //Tries to find a number between the asterisk. If there's an error, we have a false positive and fillIndex is unchanged.
                    try { fillIndex = Integer.valueOf(sub.substring(1, sub.length()-1));}
                    catch (NumberFormatException n) {}

                    //If we have an index, then replace the number with the contents of the reference at that index.
                    if (fillIndex != -1) {
                        entries[i] = entries[i].replace(sub, ("#" + (fillIndex) + " (" + constantPool[fillIndex] + ")"));
                    }
                }
            }

            //traverse(i);
        }

        //Prints out all of the constants in the constant pool.
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null) {
                System.out.println(entries[i]);
            }
        }

        //PART 4: Access flags (Private, public, etc)

        String access = "";
        int flag1 = readByte(position); position+=1; //11 + offsetConstant
        int flag2 = readByte(position); position+=1; //12 + offsetConstant
        System.out.println("Access flags are a work in progress. Here they are: " + flag1 + " " + flag2);

        //PART 5: This class

        String thisclass = "";
        int b1 = 256*readByte(position); position+=1; //13 + oC
        int b2 = readByte(position); position+=1; //14 + oC
        System.out.println("This class's name is #" + (b1 + b2 - 1));

        //PART 6: Super class

        String superclass = "";
        b1 = 256*readByte(position); position+=1; //15 + oC
        b2 = readByte(position); position+=1; //16 + oC
        System.out.println("This class extends #" + (b1 + b2 - 1));

        //PART 7: Interfaces (Count + list)

        int count = (readByte(position) * 256) + readByte(position+1);
        position+=2; //18 + oC

        int offsetInterface = 0;
        String implement = "";
        for (int i = 0; i < count; i++) {
            int index = (readByte(position + offsetInterface) * 256) + readByte(position + offsetInterface + 1);
            offsetInterface+=2;

            //implement+=constantPool[index-1] + ", ";
            implement+=index + ", ";
        }
        position+=offsetInterface; //18 + oC + oI
        if (implement.equals("")) { System.out.println("This class doesn't implement anything."); }
        else System.out.println("This class implements " + implement);

        //PART 8: Fields (Count + list)
        //PART 9: Methods (Count + list)
        //PART 10: Attributes (Code, Exceptions, other very interesting stuff)

        System.out.println();
        System.out.println();
    }

    //Reads the byte at position "offset" in the array, and adds 128 to it so that it's in the range 0-255.
    //Acceptable range: 0, 1, 2... length-1
    private static int readByte(int offset) {
        return (contentsHex.get(offset)) + 128;
    }

    //Keeps on going through references until a basic type is returned, without references.
    private static void traverse(int offset) {
        String toWorkWith = entries[offset];
        int first = -1; int second = -1;
        if (toWorkWith != null) {
            first = toWorkWith.indexOf("*");
            second = toWorkWith.indexOf("*", first + 1);
        }
        String sub = null;
        int newIndex = -1;

        //while (toWorkWith != null && toWorkWith.indexOf("*") != -1 && (second - first) > 1) {
        int count = 0;
        while (first != -1 && second != -1 && (second - first) > 1) {
            if (count > 10) {
                first = -1;
                System.out.println("Taking a while with " + entries[offset]);
                System.out.println(newIndex + " " + first + " " + second);

                break;
            }
            sub = toWorkWith.substring(first, second+1);
            try {newIndex = Integer.valueOf(sub.substring(1, sub.length()-1)) - 1;}
            catch (NumberFormatException n) {}

            toWorkWith = entries[newIndex];
            first = toWorkWith.indexOf("*");
            second = toWorkWith.indexOf("*", first + 1);

            count++;
        }

        if (sub != null && newIndex != -1) {
            entries[offset] = entries[offset].replace(sub, ("#" + (newIndex+1) + " (" + constantPool[newIndex] + ")"));        
        }
        //}

        //return toWorkWith;
    }
}

/*
 *         //Bytes 0 and 1: Access information
if (j == 0) { numInfo+=(temp*256); } 
else if (j == 1) { 
numInfo+=temp; 
switch(numInfo) {
case 1 : info+="public ";    break;
case 2 : info+="private ";   break;
case 4 : info+="protected "; break;
case 8 : info+="static ";    break;
case 10: info+="final ";     break;
case 40: info+="volitile ";  break;
case 80: info+="transient "; break;
default: info+="unknown:" + numInfo + " ";
}

if ((numInfo & 1) == 1) { info+="public ";}
if ((numInfo & 2) == 1) { info+="public ";}
if ((numInfo & 4) == 1) { info+="public ";}
if ((numInfo & 8) == 1) { info+="public ";}
if ((numInfo & 16) == 1) { info+="public ";}
if ((numInfo & 32) == 1) { info+="public ";}
if ((numInfo & 1) == 1) { info+="public ";}
if ((numInfo & 1) == 1) { info+="public ";}
if ((numInfo & 1) == 1) { info+="public ";}                        
}

numInfo = 0;

//Bytes 2 and 3: Index of name
if (j == 2) { numInfo+=(temp*256); }
else if (j == 3) { numInfo+=temp; info+=numInfo + " ";}

numInfo = 0;

//Bytes 4 and 5: Index of descriptor
if (j == 4) { numInfo+=(temp*256); }
else if (j == 5) { numInfo+=temp; info+=numInfo + " ";}

numInfo = 0;                    

//Bytes 6 and 7: Number of attributes
if (j == 6) { numInfo+=(temp*256); }
else if (j == 7) { numInfo+=temp; info+=numInfo + " ";}     

//Goes through the list of attributes
for (int k = 0; k < numInfo; k++) {

}

break; 
 */