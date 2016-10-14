/**
 * 
 */
package org.ngbw.sdk.api.conversion;

import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.common.util.Resource;

/**
 * @author hannes
 *
 */
public interface ConversionRegistryBuilder {

	public ConversionRegistry buildRegistry(Resource cfg, CoreRegistry coreRegistry);

}
