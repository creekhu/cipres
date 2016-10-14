//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.03.28 at 05:22:06 PM PDT 
//


package org.ngbw.pise.commandrenderer.pise;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{}prompt"/>
 *         &lt;element ref="{}info"/>
 *         &lt;element ref="{}format"/>
 *         &lt;element ref="{}vdef"/>
 *         &lt;element ref="{}group"/>
 *         &lt;element ref="{}vlist"/>
 *         &lt;element ref="{}flist"/>
 *         &lt;element ref="{}comment"/>
 *         &lt;element ref="{}seqfmt"/>
 *         &lt;element ref="{}seqtype"/>
 *         &lt;element ref="{}ctrls"/>
 *         &lt;element ref="{}precond"/>
 *         &lt;element ref="{}paramfile"/>
 *         &lt;element ref="{}filenames"/>
 *         &lt;element ref="{}scalemin"/>
 *         &lt;element ref="{}scalemax"/>
 *         &lt;element ref="{}scaleinc"/>
 *         &lt;element ref="{}separator"/>
 *         &lt;element ref="{}size"/>
 *         &lt;element ref="{}pipe"/>
 *         &lt;element ref="{}withpipe"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "promptOrInfoOrFormat"
})
@XmlRootElement(name = "attributes")
public class Attributes {

    @XmlElements({
        @XmlElement(name = "vdef", type = Vdef.class),
        @XmlElement(name = "paramfile", type = Paramfile.class),
        @XmlElement(name = "separator", type = Separator.class),
        @XmlElement(name = "format", type = Format.class),
        @XmlElement(name = "info", type = Info.class),
        @XmlElement(name = "size", type = Size.class),
        @XmlElement(name = "group", type = Group.class),
        @XmlElement(name = "vlist", type = Vlist.class),
        @XmlElement(name = "scaleinc", type = Scaleinc.class),
        @XmlElement(name = "seqtype", type = Seqtype.class),
        @XmlElement(name = "filenames", type = Filenames.class),
        @XmlElement(name = "scalemin", type = Scalemin.class),
        @XmlElement(name = "comment", type = Comment.class),
        @XmlElement(name = "ctrls", type = Ctrls.class),
        @XmlElement(name = "pipe", type = Pipe.class),
        @XmlElement(name = "seqfmt", type = Seqfmt.class),
        @XmlElement(name = "flist", type = Flist.class),
        @XmlElement(name = "withpipe", type = Withpipe.class),
        @XmlElement(name = "precond", type = Precond.class),
        @XmlElement(name = "scalemax", type = Scalemax.class),
        @XmlElement(name = "prompt", type = Prompt.class)
    })
    protected List<Object> promptOrInfoOrFormat;

    /**
     * Gets the value of the promptOrInfoOrFormat property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the promptOrInfoOrFormat property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPromptOrInfoOrFormat().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Vdef }
     * {@link Paramfile }
     * {@link Separator }
     * {@link Format }
     * {@link Info }
     * {@link Size }
     * {@link Group }
     * {@link Vlist }
     * {@link Scaleinc }
     * {@link Seqtype }
     * {@link Filenames }
     * {@link Scalemin }
     * {@link Comment }
     * {@link Ctrls }
     * {@link Pipe }
     * {@link Seqfmt }
     * {@link Flist }
     * {@link Withpipe }
     * {@link Precond }
     * {@link Scalemax }
     * {@link Prompt }
     * 
     * 
     */
    public List<Object> getPromptOrInfoOrFormat() {
        if (promptOrInfoOrFormat == null) {
            promptOrInfoOrFormat = new ArrayList<Object>();
        }
        return this.promptOrInfoOrFormat;
    }

}
