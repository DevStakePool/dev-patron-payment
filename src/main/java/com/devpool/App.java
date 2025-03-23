package com.devpool;

import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.backend.koios.KoiosBackendService;
import com.bloxbean.cardano.client.cip.cip20.MessageMetadata;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.devpool.service.CsvReaderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import static com.bloxbean.cardano.client.backend.koios.Constants.KOIOS_MAINNET_URL;

/**
 * Main class
 */
@Slf4j
public class App {
    private static final String HW_ADDR = "addr1qxthrv5gw2mme5fdwwz7az5er8f2s8k7rmyf7z8u9v353fhenvmnavwv0xtlw7998wfk944qq6r6lhq75y03p0myzhmsv39e4u";
    private static final String APP_NAME = "DEV Patron Payment";
    private static final Options OPTIONS = new Options();
    private static final HelpFormatter FORMATTER = new HelpFormatter();

    static {
        OPTIONS.addOption("h", "help", false, "Show help");
        OPTIONS.addOption("c", "csv", true, "Patrons CSV file");
        OPTIONS.addOption("a", "addr", true, "Payment Address addr1...");
        OPTIONS.addOption("m", "message", true, "TX Metadata Message");
    }

    public static void showHelpAndExit() {
        FORMATTER.printHelp(APP_NAME, OPTIONS);
        System.exit(1);
    }

    public void start(String[] args) throws Exception {
        String csvFileArg = null;
        String addrArg = null;
        String messageArg = null;

        try {
            var cmd = new DefaultParser().parse(OPTIONS, args);
            if (cmd.hasOption("h")) {
                showHelpAndExit();
            }

            csvFileArg = cmd.getOptionValue("c");
            addrArg = cmd.getOptionValue("a");
            messageArg = cmd.getOptionValue("m");

            if (csvFileArg == null) {
                log.error("CSV file not provided");
                showHelpAndExit();
            }

        } catch (ParseException e) {
            log.error("Error: {}", e.getMessage());
            showHelpAndExit();
        }

        var csvService = new CsvReaderService();
        var patrons = csvService.readCsvFile(csvFileArg.trim());
        var hwAddr = new Address(HW_ADDR);
        if (addrArg != null) {
            log.info("Using payment address from args {}", addrArg.trim());
            hwAddr = new Address(addrArg.trim());
        }

        var tx = new Tx().from(hwAddr.getAddress())
                .withChangeAddress(hwAddr.getAddress());

        if (messageArg != null) {
            tx.attachMetadata(MessageMetadata.create().add(messageArg.trim()));
        }

        patrons.forEach(p -> tx.payToAddress(p.address(), Amount.ada(p.ada())));

        var backendService = new KoiosBackendService(KOIOS_MAINNET_URL);
        var lastBlock = backendService.getBlockService().getLatestBlock().getValue();

        log.debug("last block height: {}", lastBlock);
        var txOutput = new QuickTxBuilder(backendService).compose(tx)
                .feePayer(hwAddr.getAddress())
                .validTo(lastBlock.getSlot() + (60 * 60)) // 1H TTL
                .build().serializeToHex();

        log.info("TX (copy/paste this into Eternl wallet): {}", txOutput);
    }

    public static void main(String[] args) throws Exception {
        log.info("Starting...");
        new App().start(args);
    }
}
