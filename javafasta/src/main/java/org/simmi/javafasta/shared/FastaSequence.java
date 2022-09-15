package org.simmi.javafasta.shared;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;

public class FastaSequence {
    String name;
    String id;
    String group;
    StringBuilder sb;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSequenceString() {
        return sb.toString();
    }

    public void setSequenceString(String seq) {
        this.sb = new StringBuilder(seq);
    }

    public void writeSequence( Appendable fw, int gap, boolean italic ) throws IOException {
        if( italic ) fw.append("><i>"+getName()+"</i>\n");
        else fw.append(">"+getName()+"\n");
        for( int k = 0; k < sb.length(); k+=gap ) {
            int m = Math.min(sb.length(), k+gap);
            String substr = sb.substring(k, m);
            //(seq.sb.length() == k+70 ? "")
            fw.append( substr+"\n" );
        }
    }

    public int writeSequence(Writer fw, int gap, boolean italic) throws IOException {
        return writeSequence(fw,gap,italic,false);
    }

    public int writeSequence( Writer fw, int gap, boolean italic, boolean group ) throws IOException {
        String name = group && getGroup()!=null ? getName()+"|"+getGroup() : getName();
        return writeSequence(fw, gap, italic, group, name);
    }

    public int writeSequence( Writer fw, int gap, boolean italic, boolean group, String name ) throws IOException {
        if( italic ) fw.write("><i>"+name+"</i>\n");
        else fw.write(">"+name+"\n");
        for( int k = 0; k < sb.length(); k+=gap ) {
            int m = Math.min(sb.length(), k+gap);
            String substr = sb.substring(k, m);
            //(seq.sb.length() == k+70 ? "")
            fw.write( substr+"\n" );
        }
        return sb.length();
    }

    public void writeIdSequence( Writer fw, int gap, boolean italic ) throws IOException {
        if( italic ) fw.write("><i>"+id+"</i>\n");
        else fw.write(">"+id+"\n");
        for( int k = 0; k < sb.length(); k+=gap ) {
            int m = Math.min(sb.length(), k+gap);
            String substr = sb.substring(k, m);
            //(seq.sb.length() == k+70 ? "")
            fw.write( substr+"\n" );
        }
    }

    public String asFasta() throws IOException {
        StringWriter sw = new StringWriter();
        writeSequence(sw);
        return sw.toString();
    }

    public String asSplitFasta(Optional<Integer> segment) throws IOException {
        StringWriter sw = new StringWriter();
        writeSplitSequence(sw, segment);
        return sw.toString();
    }

    public String asSplitFasta() throws IOException {
        StringWriter sw = new StringWriter();
        writeSplitSequence(sw, Optional.empty());
        return sw.toString();
    }

    public void writeIdSequence( Writer fw ) throws IOException {
        writeIdSequence( fw, 70, false );
    }

    public void writeSequence( Appendable fw ) throws IOException {
        writeSequence( fw, 70, false );
    }

    public int writeSequence( Writer fw ) throws IOException {
        return writeSequence( fw, 70, false );
    }

    public int writeSequence( Writer fw, String name ) throws IOException {
        return writeSequence( fw, 70, false, false, name );
    }

    public void writeSequence(Writer fw, boolean italic) throws IOException {
        writeSequence(fw,italic,false);
    }

    public void writeSequence( Writer fw, boolean italic, boolean group ) throws IOException {
        writeSequence( fw, 70, italic, group );
    }

    public void writeSplitSequence( Writer fw, Optional<Integer> segment ) throws IOException {
        int total = 0;
        int seg = segment.orElse(1020);
        while( total < sb.length() ) {
            fw.write(">"+getName()+"_"+total+"\n");
            int end = Math.min(total+1020,sb.length());
            for( int k = total; k < end; k+=70 ) {
                int m = Math.min(end, k+70);
                String substr = sb.substring(k, m);
                //(seq.sb.length() == k+70 ? "")
                fw.write( substr+"\n" );
            }
            total += seg;
        }
    }
}
