package org.guppy4j;

import java.net.URL;

/**
 * Lists sub-directories
 */
public interface DirectoryLister {

    Iterable<String> getSubDirectories(URL url);
}
