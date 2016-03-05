package net.ripe.rpki.validator;

import net.ripe.rpki.validator.cli.CommandLineOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;
import java.util.Map;

@Aspect
public class CommandLineOptionsAspect
{
    private static final String PREFIX_MAP = "prefix-map";

    @Pointcut( "target(net.ripe.rpki.validator.cli.CommandLineOptions) && execution(void addOptions())" )
    public void addOptions()
    {
    }

    @After( "addOptions()" )
    public void afterAddOptions( JoinPoint jp ) throws Exception
    {
        Field optionsField = CommandLineOptions.class.getDeclaredField( "options" );
        optionsField.setAccessible( true );
        Options options = (Options) optionsField.get( jp.getTarget() );
        options.addOption( "x", PREFIX_MAP, true, "Prefix Mapping for rsync URI to directory/file on file system" );
        options.getOption( PREFIX_MAP ).setArgs( Option.UNLIMITED_VALUES );
    }

    @Pointcut( "target(net.ripe.rpki.validator.cli.CommandLineOptions) && execution(void parse(..)) && args(args)" )
    public void parse( String... args )
    {
    }

    @After( "parse(args)" )
    public void afterParse( JoinPoint jp, String... args ) throws Exception
    {
        Field optionsField = CommandLineOptions.class.getDeclaredField( "options" );
        optionsField.setAccessible( true );
        Options options = (Options) optionsField.get( jp.getTarget() );
        GnuParser parser = new GnuParser();
        CommandLine commandLine = parser.parse( options, args );
        if ( commandLine.hasOption( PREFIX_MAP ) )
        {
            for ( String prefixEntry : commandLine.getOptionValues( PREFIX_MAP ) )
            {
                String[] prefixPair = prefixEntry.split( "=", 2 );
                if ( prefixPair.length != 2 )
                {
                    continue;
                }
                RsyncFsMapper.addRsyncFsMapping( prefixPair[ 0 ], prefixPair[ 1 ] );
            }
        }
    }
}
