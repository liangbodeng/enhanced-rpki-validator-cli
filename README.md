# A Demo of AspectJ usage - Enhance the RIPE RPKI Validator

## What is this?

  This is a demo of how you can use AspectJ to cleanly enhance a third-party library. You may not have the access to the source code, or you prefer not to modify the source code.

## Why the enhancement?

  When running test, we want to verify a local generated RPKI repository without starting rsync.

## Enhancements explained

  We enhanced two classes: Rsync and CommandLineOptions.
    
  - For Rsync: execute method is enhanced
  - For CommandLineOptions: addOptions and parse methods are enhanced
  - We also added a RsyncFsMapper to store the mapping of rsync <-> file system

## Result: now we can use local file system to simulate rsync with the mapping specified with -x (multiple mappings allowed)

    java -jar enhanced-rpki-validator-cli.jar -t test.tal -o output -x rsync://test01.rpki.example.com:12345/repository=/tmp/test01repo
