package net.ripe.rpki.validator;

import net.ripe.rpki.commons.rsync.Rsync;
import org.apache.commons.io.FileUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Aspect
public class RsyncAspect
{
    @Pointcut( "target(net.ripe.rpki.commons.rsync.Rsync) && execution(int execute())" )
    public void execute()
    {
    }

    @Around( "execute()" )
    public Object aroundExecute( ProceedingJoinPoint pcp ) throws Throwable
    {
        Rsync rsync = (Rsync) pcp.getTarget();
        String source = rsync.getSource();
        String fsSource = RsyncFsMapper.rsyncPathToFsPath( source );
        if ( fsSource == null )
        {
            return pcp.proceed();
        }

        File sourceFile = new File( fsSource );
        File destinationFile = new File( rsync.getDestination() );
        if ( sourceFile.equals( destinationFile ) )
        {
            return 0;
        }

        try
        {
            if ( sourceFile.isDirectory() )
            {
                FileUtils.copyDirectory( sourceFile, destinationFile );
                return 0;
            }
            else if ( sourceFile.isFile() )
            {
                FileUtils.copyFile( sourceFile, destinationFile );
                return 0;
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        return pcp.proceed();
    }
}
