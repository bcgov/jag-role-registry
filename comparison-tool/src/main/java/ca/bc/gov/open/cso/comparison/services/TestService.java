package ca.bc.gov.open.cso.comparison.services;

import ca.bc.gov.open.cso.*;
import ca.bc.gov.open.cso.comparison.config.DualProtocolSaajSoapMessageFactory;
import ca.bc.gov.open.cso.comparison.config.WebServiceSenderWithAuth;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Service
public class TestService {
    private final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

    private final WebServiceSenderWithAuth webServiceSenderWithAuth;

    private final Javers javers = JaversBuilder.javers().build();

    @Autowired
    public TestService(WebServiceSenderWithAuth webServiceSenderWithAuth) {
        this.webServiceSenderWithAuth = webServiceSenderWithAuth;
    }

    @Value("${host.api-host}")
    private String apiHost;

    @Value("${host.wm-host}")
    private String wmHost;

    @Value("${host.username}")
    private String username;

    @Value("${host.password}")
    private String password;

    private String RAID = "83.0001";
    private String partId = RAID;
    private Instant dtm = Instant.now();

    private PrintWriter fileOutput;
    private static String outputDir = "comparison-tool/results/";

    private int overallDiff = 0;

    public void runCompares() throws IOException {
        System.out.println("INFO: CSO Role Registry Diff testing started");
        getRolesForIdentityCompare();
        //    getRolesForIdentifierCompare();
        //    getRolesForApplicationCompare();
    }

    private void getRolesForIdentityCompare() throws IOException {
        GetRolesForIdentity request = new GetRolesForIdentity();
        GetRolesForIdentityResponse getRolesForIdentityResponse = new GetRolesForIdentityResponse();
        int diffCounter = 0;
        int recCounter = 0;
        InputStream inputIds = getClass().getResourceAsStream("/getRolesForIdentityInputs.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput = new PrintWriter(outputDir + "getRolesForIdentity.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String[] inputs = scanner.nextLine().split(",");
            request.setAccountIdentifier(inputs[0]);
            request.setApplication(inputs[1]);
            request.setDomain(inputs[2]);
            request.setUserIdentifier(inputs[3]);
            request.setIdentifierType(inputs[4]);

            System.out.println(
                    "\nINFO: getRolesForIdentity with AccountIdentifier "
                            + inputs[0]
                            + " Application "
                            + inputs[1]
                            + " Domain "
                            + inputs[2]
                            + " UserIdentifier "
                            + inputs[3]
                            + " IdentifierType "
                            + inputs[4]);

            if (!compare(
                    getRolesForIdentityResponse, request, new String[] {"ca.bc.gov.open.cso"})) {
                fileOutput.println(
                        "\nINFO: getRolesForIdentity with AccountIdentifier"
                                + inputs[0]
                                + " Application "
                                + inputs[1]
                                + " Domain "
                                + inputs[2]
                                + " UserIdentifier "
                                + inputs[3]
                                + " IdentifierType "
                                + inputs[4]);
                diffCounter++;
                System.out.println("Record Count: " + recCounter++);
            } // end if
        } // end loop
        System.out.println(
                "########################################################\n"
                        + "INFO: getRolesForIdentity Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");
        fileOutput.println(
                "########################################################\n"
                        + "INFO: getRolesForIdentity Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");
        overallDiff += diffCounter;
        fileOutput.close();
    }

    private void getRolesForIdentifierCompare() throws IOException {
        GetRolesForIdentifier request = new GetRolesForIdentifier();
        GetRolesForIdentifierResponse getRolesForIdentifierResponse =
                new GetRolesForIdentifierResponse();
        int diffCounter = 0;
        int recCounter = 0;
        InputStream inputIds = getClass().getResourceAsStream("/getRolesForIdentifierInputs.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput =
                new PrintWriter(outputDir + "getRolesForIdentifier.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String[] inputs = scanner.nextLine().split(",");
            request.setApplication(inputs[0]);
            request.setDomain(inputs[1]);
            request.setIdentifier(inputs[2]);
            request.setIdentifierType(inputs[3]);

            System.out.println(
                    "\nINFO: getRolesForIdentifier with Application "
                            + inputs[0]
                            + " Domain "
                            + inputs[1]
                            + " Identifier "
                            + inputs[2]
                            + " IdentifierType "
                            + inputs[3]);

            if (!compare(
                    getRolesForIdentifierResponse, request, new String[] {"ca.bc.gov.open.cso"})) {
                fileOutput.println(
                        "\nINFO: getRolesForIdentifier with Application"
                                + inputs[0]
                                + " Domain "
                                + inputs[1]
                                + " Identifier "
                                + inputs[2]
                                + " IdentifierType "
                                + inputs[3]);
                diffCounter++;
                System.out.println("Record Count: " + recCounter++);
            } // end if
        } // end loop
        System.out.println(
                "########################################################\n"
                        + "INFO: getRolesForIdentifier Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");
        fileOutput.println(
                "########################################################\n"
                        + "INFO: getRolesForIdentifier Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");
        overallDiff += diffCounter;
        fileOutput.close();
    }

    private void getRolesForApplicationCompare() throws IOException {
        GetRolesForApplication request = new GetRolesForApplication();
        GetRolesForApplicationResponse getRolesForApplicationResponse =
                new GetRolesForApplicationResponse();
        int diffCounter = 0;
        int recCounter = 0;
        InputStream inputIds = getClass().getResourceAsStream("/getRolesForApplicationInputs.csv");
        assert inputIds != null;
        Scanner scanner = new Scanner(inputIds);
        fileOutput =
                new PrintWriter(outputDir + "getRolesForApplication.txt", StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            String[] inputs = scanner.nextLine().split(",");
            request.setApplication(inputs[0]);
            request.setDomain(inputs[1]);
            request.setType(inputs[2]);

            System.out.println(
                    "\nINFO: getRolesForApplication with Application "
                            + inputs[0]
                            + " Domain "
                            + inputs[1]
                            + " Type "
                            + inputs[2]);

            if (!compare(
                    getRolesForApplicationResponse, request, new String[] {"ca.bc.gov.open.cso"})) {
                fileOutput.println(
                        "\nINFO: getRolesForApplication with Application"
                                + inputs[0]
                                + " Domain "
                                + inputs[1]
                                + " Type "
                                + inputs[2]);
                diffCounter++;
                System.out.println("Record Count: " + recCounter++);
            } // end if
        } // end loop
        System.out.println(
                "########################################################\n"
                        + "INFO: getRolesForApplication Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");
        fileOutput.println(
                "########################################################\n"
                        + "INFO: getRolesForApplication Completed there are "
                        + diffCounter
                        + " diffs\n"
                        + "########################################################");
        overallDiff += diffCounter;
        fileOutput.close();
    }

    public <T, G> boolean compare(T response, G request, String[] contextPath) {

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();

        DualProtocolSaajSoapMessageFactory saajSoapMessageFactory =
                new DualProtocolSaajSoapMessageFactory();
        saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
        saajSoapMessageFactory.afterPropertiesSet();

        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setCredentials(
                new UsernamePasswordCredentials(username, password));

        webServiceTemplate.setMessageSender(webServiceSenderWithAuth);
        webServiceTemplate.setMessageFactory(saajSoapMessageFactory);
        jaxb2Marshaller.setContextPaths(contextPath);
        webServiceTemplate.setMarshaller(jaxb2Marshaller);
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller);
        webServiceTemplate.afterPropertiesSet();

        T resultObjectWM = null;
        T resultObjectAPI = null;

        try {
            resultObjectWM = (T) webServiceTemplate.marshalSendAndReceive(wmHost, request);
            resultObjectAPI = (T) webServiceTemplate.marshalSendAndReceive(apiHost, request);
            Thread.sleep(5000);

        } catch (Exception e) {
            System.out.println("ERROR: Failed to send request... " + e);
            fileOutput.println("ERROR: Failed to send request... " + e);
        }

        Diff diff = javers.compare(resultObjectAPI, resultObjectWM);

        String responseClassName = response.getClass().getName();
        if (diff.hasChanges()) {
            printDiff(diff);
            return false;
        } else {
            if (resultObjectAPI == null && resultObjectWM == null)
                System.out.println(
                        "WARN: "
                                + responseClassName.substring(
                                        responseClassName.lastIndexOf('.') + 1)
                                + ": NULL responses");
            else
                System.out.println(
                        "INFO: "
                                + responseClassName.substring(
                                        responseClassName.lastIndexOf('.') + 1)
                                + ": No Diff Detected");
            return true;
        }
    }

    private void printDiff(Diff diff) {
        List<Change> actualChanges = new ArrayList<>(diff.getChanges());

        actualChanges.removeIf(
                c -> {
                    if (c instanceof ValueChange) {
                        return ((ValueChange) c).getLeft() == null
                                && ((ValueChange) c).getRight().toString().isBlank();
                    }

                    return false;
                });

        int diffSize = actualChanges.size();

        if (diffSize == 0) {
            System.out.println("Only null and blank changes detected");
            return;
        }

        String[] header = new String[] {"Property", "API Response", "WM Response"};
        String[][] table = new String[diffSize + 1][3];
        table[0] = header;

        for (int i = 0; i < diffSize; ++i) {
            Change ch = diff.getChanges().get(i);

            if (ch instanceof ListChange) {
                String apiVal =
                        ((ListChange) ch).getLeft() == null
                                ? "null"
                                : ((ListChange) ch).getLeft().toString();
                String wmVal =
                        ((ListChange) ch).getRight() == null
                                ? "null"
                                : ((ListChange) ch).getRight().toString();
                table[i + 1][0] = ((ListChange) ch).getPropertyNameWithPath();
                table[i + 1][1] = apiVal;
                table[i + 1][2] = wmVal;
            } else if (ch instanceof ValueChange) {
                String apiVal =
                        ((ValueChange) ch).getLeft() == null
                                ? "null"
                                : ((ValueChange) ch).getLeft().toString();
                String wmVal =
                        ((ValueChange) ch).getRight() == null
                                ? "null"
                                : ((ValueChange) ch).getRight().toString();
                table[i + 1][0] = ((ValueChange) ch).getPropertyNameWithPath();
                table[i + 1][1] = apiVal;
                table[i + 1][2] = wmVal;
            }
        }

        boolean leftJustifiedRows = false;
        int totalColumnLength = 10;
        /*
         * Calculate appropriate Length of each column by looking at width of data in
         * each column.
         *
         * Map columnLengths is <column_number, column_length>
         */
        Map<Integer, Integer> columnLengths = new HashMap<>();
        Arrays.stream(table)
                .forEach(
                        a ->
                                Stream.iterate(0, (i -> i < a.length), (i -> ++i))
                                        .forEach(
                                                i -> {
                                                    if (columnLengths.get(i) == null) {
                                                        columnLengths.put(i, 0);
                                                    }
                                                    if (columnLengths.get(i) < a[i].length()) {
                                                        columnLengths.put(i, a[i].length());
                                                    }
                                                }));

        for (Map.Entry<Integer, Integer> e : columnLengths.entrySet()) {
            totalColumnLength += e.getValue();
        }
        fileOutput.println("=".repeat(totalColumnLength));
        System.out.println("=".repeat(totalColumnLength));

        final StringBuilder formatString = new StringBuilder("");
        String flag = leftJustifiedRows ? "-" : "";
        columnLengths.entrySet().stream()
                .forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
        formatString.append("|\n");

        Stream.iterate(0, (i -> i < table.length), (i -> ++i))
                .forEach(
                        a -> {
                            fileOutput.printf(formatString.toString(), table[a]);
                            System.out.printf(formatString.toString(), table[a]);
                        });

        fileOutput.println("=".repeat(totalColumnLength));
        System.out.println("=".repeat(totalColumnLength));
    }
}
