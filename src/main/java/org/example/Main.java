package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FileReader fileReader = new FileReader("test.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileWriter fileWriter = new FileWriter("0_revision_result.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


            bufferedWriter.write("set context person creator;");
            bufferedWriter.newLine();
            bufferedWriter.write("tcl;");
            bufferedWriter.newLine();
            bufferedWriter.write("mql start transaction;");
            bufferedWriter.newLine();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String lineWithEpc = line + "_1-epc";
                String lineWithOwn = line + "_1-own";

                String commandWithEpc = "set DocSetRev1 [mql temp query bus IMS_DocumentSetRevision * * where \"policy != 'Version' && to\\[IMS_Revision\\].from.attribute\\[IMS_Code\\]=='" + line + "' && project =='JSC EC ASE CS'\" select id dump tcl];\n" +
                        "set DocSet [mql temp query bus IMS_DocumentSetRevision * * where to\\[IMS_Revision\\].from.attribute\\[IMS_Code\\]=='" + line + "' select to\\[IMS_Revision\\].from.id dump tcl];\n" +
                        "set DocSetID [lindex $DocSet 0 3 0];\n" +
                        "set id [lindex $DocSetRev1 0 3 0];\n" +
                        "mql revise bus IMS_DocumentSetRevision '" + lineWithEpc + "' 0 bus $id;\n" +
                        "mql mod bus $id name '" + lineWithEpc + "_1' revision 1;\n" +
                        "mql connect bus IMS_DocumentSetRevision '" + lineWithEpc + "' 0 rel IMS_Revision from $DocSetID\n";
                bufferedWriter.write(commandWithEpc);
                bufferedWriter.newLine();
                bufferedWriter.write("############");
                bufferedWriter.newLine();

                String commandWithOwn = "set DocSetRev1 [mql temp query bus IMS_DocumentSetRevision * * where \"policy != 'Version' && to\\[IMS_Revision\\].from.attribute\\[IMS_Code\\]=='" + line + "' && project =='Nuclear Power Plant Authority CS'\" select id dump tcl];\n" +
                        "set DocSet [mql temp query bus IMS_DocumentSetRevision * * where to\\[IMS_Revision\\].from.attribute\\[IMS_Code\\]=='" + line + "' select to\\[IMS_Revision\\].from.id dump tcl];\n" +
                        "set DocSetID [lindex $DocSet 0 3 0];\n" +
                        "set id [lindex $DocSetRev1 0 3 0];\n" +
                        "mql revise bus IMS_DocumentSetRevision '" + lineWithOwn + "' 0 bus $id;\n" +
                        "mql mod bus $id name '" + lineWithOwn + "_1' revision 1;\n" +
                        "mql connect bus IMS_DocumentSetRevision '" + lineWithOwn + "' 0 rel IMS_Revision from $DocSetID\n";
                bufferedWriter.write(commandWithOwn);
                bufferedWriter.newLine();
                bufferedWriter.write("############");
                bufferedWriter.newLine();
            }

            bufferedWriter.write("mql commit transaction;");
            bufferedWriter.newLine();

            bufferedReader.close();
            fileReader.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



