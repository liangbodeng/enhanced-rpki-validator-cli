package net.ripe.rpki.validator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RsyncFsMapper
{
    private static Map<String, String> rsyncFsMap = new HashMap<String, String>();

    public static void addRsyncFsMapping( String rsyncPath, String fsPath )
    {
        if ( !rsyncPath.startsWith( "rsync://" ) )
        {
            throw new RuntimeException( "Rsync path `" + rsyncPath + "` does not start with rsync://" );
        }

        rsyncFsMap.put( rsyncPath, fsPath );
    }

    public static String rsyncPathToFsPath( String rsyncPath )
    {
        for ( String rsyncPrefix : rsyncFsMap.keySet() )
        {
            if ( !rsyncPath.contains( rsyncPrefix ) )
            {
                continue;
            }

            String fsPath = rsyncPath.replace( rsyncPrefix, rsyncFsMap.get( rsyncPrefix ) );
            File f = new File( fsPath );
            if ( !f.exists() )
            {
                continue;
            }

            return fsPath;
        }

        return null;
    }
}
