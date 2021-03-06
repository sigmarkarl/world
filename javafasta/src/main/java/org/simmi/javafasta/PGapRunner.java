package org.simmi.javafasta;

import org.simmi.javafasta.shared.Sequence;
import org.simmi.javafasta.shared.Sequences;
import org.simmi.javafasta.shared.Serifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PGapRunner implements Runnable {
    Sequences sequences;
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

    public void setSequences(Sequences seqs) {
        this.sequences = seqs;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            Path inputFolder = pgapPath.resolve(sequences.getName());
            Files.createDirectories(inputFolder);
            Path inputYaml = inputFolder.resolve("input.yaml");
            Files.writeString(inputYaml, inputyamlTemplate);
            Path inputFasta = inputFolder.resolve("input.fasta");
            var writer = Files.newBufferedWriter(inputFasta);
            Map<String, Sequence> contmap = serifier.appendSequenceInJavaFasta(sequences, false);
            contmap.entrySet().stream().map(Map.Entry::getValue).filter(v -> v.length() >= 200).forEach(s -> {
                try {
                    s.writeSequence(writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Path submolYaml = inputFolder.resolve("submol.yaml");
            Files.writeString(submolYaml, submolyamlTemplate.replace("${species}", "Thermus brockianus").replace("${accession}", "2978"));

            ProcessBuilder processBuilder = new ProcessBuilder(pgapPath.resolve("scripts/pgap.py").toString(),"-d","-r","-o", sequences.getName()+"_results",inputYaml.toString());
            processBuilder.directory(pgapPath.toFile());
            Process process = processBuilder.start();
            executorService.submit(() -> {
                try {
                    process.getErrorStream().transferTo(System.err);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executorService.submit(() -> {
                try {
                    process.getInputStream().transferTo(System.out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
