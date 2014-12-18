//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.10 at 10:36:12 AM CAT 
//


package org.esaude.dmt.config.schema;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence>
 *         &lt;element name="matching_input" type="{http://schema.config.dmt.esaude.org}fileType"/>
 *         &lt;element name="targetDs" type="{http://schema.config.dmt.esaude.org}datasourceType"/>
 *         &lt;element name="sourceDs" type="{http://schema.config.dmt.esaude.org}datasourceType"/>
 *         &lt;element name="log_output" type="{http://schema.config.dmt.esaude.org}fileType"/>
 *         &lt;element name="tree_limit" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="allow_commit" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="reset_process" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "matchingInput",
    "targetDs",
    "sourceDs",
    "logOutput",
    "treeLimit",
    "allowCommit",
    "resetProcess"
})
@XmlRootElement(name = "config")
public class Config {

    @XmlElement(name = "matching_input", required = true)
    protected FileType matchingInput;
    @XmlElement(required = true)
    protected DatasourceType targetDs;
    @XmlElement(required = true)
    protected DatasourceType sourceDs;
    @XmlElement(name = "log_output", required = true)
    protected FileType logOutput;
    @XmlElement(name = "tree_limit", required = true)
    protected BigInteger treeLimit;
    @XmlElement(name = "allow_commit")
    protected boolean allowCommit;
    @XmlElement(name = "reset_process")
    protected boolean resetProcess;

    /**
     * Gets the value of the matchingInput property.
     * 
     * @return
     *     possible object is
     *     {@link FileType }
     *     
     */
    public FileType getMatchingInput() {
        return matchingInput;
    }

    /**
     * Sets the value of the matchingInput property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileType }
     *     
     */
    public void setMatchingInput(FileType value) {
        this.matchingInput = value;
    }

    /**
     * Gets the value of the targetDs property.
     * 
     * @return
     *     possible object is
     *     {@link DatasourceType }
     *     
     */
    public DatasourceType getTargetDs() {
        return targetDs;
    }

    /**
     * Sets the value of the targetDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatasourceType }
     *     
     */
    public void setTargetDs(DatasourceType value) {
        this.targetDs = value;
    }

    /**
     * Gets the value of the sourceDs property.
     * 
     * @return
     *     possible object is
     *     {@link DatasourceType }
     *     
     */
    public DatasourceType getSourceDs() {
        return sourceDs;
    }

    /**
     * Sets the value of the sourceDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatasourceType }
     *     
     */
    public void setSourceDs(DatasourceType value) {
        this.sourceDs = value;
    }

    /**
     * Gets the value of the logOutput property.
     * 
     * @return
     *     possible object is
     *     {@link FileType }
     *     
     */
    public FileType getLogOutput() {
        return logOutput;
    }

    /**
     * Sets the value of the logOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileType }
     *     
     */
    public void setLogOutput(FileType value) {
        this.logOutput = value;
    }

    /**
     * Gets the value of the treeLimit property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTreeLimit() {
        return treeLimit;
    }

    /**
     * Sets the value of the treeLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTreeLimit(BigInteger value) {
        this.treeLimit = value;
    }

    /**
     * Gets the value of the allowCommit property.
     * 
     */
    public boolean isAllowCommit() {
        return allowCommit;
    }

    /**
     * Sets the value of the allowCommit property.
     * 
     */
    public void setAllowCommit(boolean value) {
        this.allowCommit = value;
    }

    /**
     * Gets the value of the resetProcess property.
     * 
     */
    public boolean isResetProcess() {
        return resetProcess;
    }

    /**
     * Sets the value of the resetProcess property.
     * 
     */
    public void setResetProcess(boolean value) {
        this.resetProcess = value;
    }

}
