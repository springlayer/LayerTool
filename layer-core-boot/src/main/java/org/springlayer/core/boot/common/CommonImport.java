package org.springlayer.core.boot.common;

import org.springlayer.core.boot.context.CurrentUserHolder;
import org.springframework.context.annotation.Import;

@Import({
        CurrentUserHolder.class
})
public class CommonImport {

}