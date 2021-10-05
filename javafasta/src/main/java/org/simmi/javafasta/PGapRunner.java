package org.simmi.javafasta;

import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Sequences;
import org.simmi.javafasta.shared.Serifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PGapRunner {
    Path pgapPath;
    Serifier serifier;

    final static String inputyamlTemplate = "fasta:\n" +
            "  class: File\n" +
            "  location: input.fasta\n" +
            "submol:\n" +
            "  class: File\n" +
            "  location: submol.yaml\n";

    final static String submolyamlTemplate = "topology: circular\n" +
            "comment: 'There is no really a biologist Arnold Schwarzenegger'\n" +
            "consortium: 'SkyNet consortium'\n" +
            "sra:\n" +
            "    - accession: '${accession}'\n" +
            "tp_assembly: true\n" +
            "organism:\n" +
            "    genus_species: '${species}' \n" +
            "    strain: 'replaceme'\n" +
            "contact_info:\n" +
            "    last_name: 'Doe'\n" +
            "    first_name: 'Jane'\n" +
            "    email: 'jane_doe@gmail.com'\n" +
            "    organization: 'Institute of Klebsiella foobarensis research'\n" +
            "    department: 'Department of Using NCBI'\n" +
            "    phone: '301-555-0245'\n" +
            "    street: '1234 Main St'\n" +
            "    city: 'Docker'\n" +
            "    postal_code: '12345'\n" +
            "    country: 'Lappland'\n" +
            "    \n" +
            "authors:\n" +
            "    -     author:\n" +
            "            first_name: 'Arnold'\n" +
            "            last_name: 'Schwarzenegger'\n" +
            "            middle_initial: 'T'\n" +
            "    -     author:\n" +
            "            first_name: 'Linda'\n" +
            "            last_name: 'Hamilton'\n" +
            "bioproject: 'PRJNA9999999'\n" +
            "biosample: 'SAMN99999999'      \n" +
            "# -- Locus tag prefix - optional. Limited to 9 letters. Unless the locus tag prefix was officially assigned by NCBI, ENA, or DDBJ, it will be replaced upon submission of the annotation to NCBI and is therefore temporary and not to be used in publications. If not provided, pgaptmp will be used.\n" +
            "locus_tag_prefix: 'tmp'\n" +
            "publications:\n" +
            "    - publication:\n" +
            "        pmid: 16397293\n" +
            "        title: 'Discrete CHARMm of Klebsiella foobarensis. Journal of Improbable Results, vol. 34, issue 13, pages: 10001-100005, 2018'\n" +
            "        status: published  # this is enum: controlled vocabulary\n" +
            "        authors:\n" +
            "            - author:\n" +
            "                first_name: 'Arnold'\n" +
            "                last_name: 'Schwarzenegger'\n" +
            "                middle_initial: 'T'\n" +
            "            - author:\n" +
            "                  first_name: 'Linda'\n" +
            "                  last_name: 'Hamilton'\n";

    public PGapRunner(Path path, Serifier serifier) {
        assert Files.exists(path);
        this.pgapPath = path;
        this.serifier = serifier;
    }

    public Process run(ExecutorService executorService, Sequences sequences) {
        try {
            Path inputFolder = pgapPath.resolve(sequences.getName());
            Files.createDirectories(inputFolder);
            Path inputYaml = inputFolder.resolve("input.yaml");
            Files.writeString(inputYaml, inputyamlTemplate);
            Path inputFasta = inputFolder.resolve("input.fasta");
            var writer = Files.newBufferedWriter(inputFasta);
            Map<String, Sequence> contmap = serifier.appendSequenceInJavaFasta(sequences, false);
            contmap.values().stream().filter(v -> v.length() >= 200).forEach(s -> {
                try {
                    s.writeSequence(writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            var seq = sequences.getName();
            if(seq.endsWith(".fna")) seq = seq.substring(0,seq.length()-4);

            Path submolYaml = inputFolder.resolve("submol.yaml");
            Files.writeString(submolYaml, submolyamlTemplate.replace("${species}", "Thermus thermophilus").replace("${accession}", "2978").replace("locus_tag_prefix: 'tmp'","locus_tag_prefix: '"+seq+"'"));

            ProcessBuilder processBuilder = new ProcessBuilder(pgapPath.resolve("scripts/pgap.py").toString(),"--verbose","-d","-r","-o", seq+"_results",inputYaml.toString());
            processBuilder.directory(pgapPath.toFile());
            Process process = processBuilder.start();
            Future<?> err = executorService.submit(() -> {
                try {
                    process.getErrorStream().transferTo(System.err);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Future<?> out = executorService.submit(() -> {
                try {
                    process.getInputStream().transferTo(System.out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return process;
            //process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
