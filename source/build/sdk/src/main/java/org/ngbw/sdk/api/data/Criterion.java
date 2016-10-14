/**
 * 
 */
package org.ngbw.sdk.api.data;

/**
 * A Criterion so far is an empty interface since I have not found 
 * anything meaningful and implementation independent methods to put in. 
 * But it still serves a purpose by typing the constructs required to 
 * computationally build a query. In short a Criterion can be anything 
 * that will permit its meaningful use in all methods that return a 
 * Criterion in org.ngbw.sdk.api.data.DataResource that can than be used 
 * to build the query's 'WHERE' clause in org.ngbw.sdk.api.data.Query	
 * addCriterion(Criterion criterion);
 * 
 * @author Roland H. Niedner <br />
 *
 */
public interface Criterion {
	//empty by design
}
