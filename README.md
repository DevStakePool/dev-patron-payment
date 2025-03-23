# dev-patron-payment
DEV pool patron epoch payment application

This is a small Java stand-alone application that is used to create a Cardano TX starting from a CSV file with multiple addresses.

See file [patron.csv](patrons.csv)

## Build
The application needs Java 21

```bash
mvn clean package
```

## Run

Execute
```bash
java -jar target/dev-patron-payment-full-0.0.1-SNAPSHOT.jar -h

```

This last command will show the following output

```
usage: DEV Patron Payment
 -a,--addr <arg>      Payment Address addr1...
 -c,--csv <arg>       Patrons CSV file
 -h,--help            Show help
 -m,--message <arg>   TX Metadata Message
```

## Example

```bash
java -jar target/dev-patron-payment-full-0.0.1-SNAPSHOT.jar \
    -c patron.csv \
    -m "Hello TX"    
```

The output will show you the HEX of the CBOR transaction. Copy/Paste this into Eternl or other Cardano
wallets to sign the TX and submit it!

