/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://jwsdp.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * https://jwsdp.dev.java.net/CDDLv1.0.html  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 */
package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.parser.SchemaDocument;
import org.xml.sax.Locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ComponentImpl implements XSComponent
{
    protected ComponentImpl( SchemaDocumentImpl _owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa ) {
        this.ownerDocument = _owner;
        this.annotation = _annon;
        this.locator = _loc;
        this.foreignAttributes = fa;
    }

    protected final SchemaDocumentImpl ownerDocument;
    public SchemaImpl getOwnerSchema() {
        if(ownerDocument==null)
            return null;
        else
            return ownerDocument.getSchema();
    }

    public SchemaDocument getSourceDocument() {
        return ownerDocument;
    }

    private final AnnotationImpl annotation;
    public final XSAnnotation getAnnotation() { return annotation; }
    
    private final Locator locator;
    public final Locator getLocator() { return locator; }

    /**
     * Either {@link ForeignAttributesImpl} or {@link List}.
     *
     * Initially it's {@link ForeignAttributesImpl}, but it's lazily turned into
     * a list when necessary.
     */
    private Object foreignAttributes;

    public List getForeignAttributes() {
        Object t = foreignAttributes;

        if(t==null)
            return Collections.EMPTY_LIST;

        if(t instanceof List)
            return (List)t;

        t = foreignAttributes = convertToList((ForeignAttributesImpl)t);
        return (List)t;
    }

    public String getForeignAttribute(String nsUri, String localName) {
        for( ForeignAttributesImpl fa : (List<ForeignAttributesImpl>)getForeignAttributes() ) {
            String v = fa.getValue(nsUri,localName);
            if(v!=null) return v;
        }
        return null;
    }

    private List convertToList(ForeignAttributesImpl fa) {
        List lst = new ArrayList();
        while(fa!=null) {
            lst.add(fa);
            fa = fa.next;
        }
        return Collections.unmodifiableList(lst);
    }
}
