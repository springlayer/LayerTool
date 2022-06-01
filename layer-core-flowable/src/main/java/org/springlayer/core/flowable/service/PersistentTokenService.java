/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springlayer.core.flowable.service;

import org.flowable.idm.api.Token;
import org.flowable.idm.api.User;

/**
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
public interface PersistentTokenService {

    /**
     * getPersistentToken
     *
     * @param tokenId ignore
     * @return ignore
     */
    Token getPersistentToken(String tokenId);

    /**
     * getPersistentToken
     *
     * @param tokenId              ignore
     * @param invalidateCacheEntry ignore
     * @return ignore
     */
    Token getPersistentToken(String tokenId, boolean invalidateCacheEntry);

    /**
     * saveAndFlush
     *
     * @param persistentToken Token
     * @return Token
     */
    Token saveAndFlush(Token persistentToken);

    /**
     * delete
     *
     * @param persistentToken ignore
     */
    void delete(Token persistentToken);

    /**
     * createToken
     *
     * @param user          ignore
     * @param remoteAddress ignore
     * @param userAgent     ignore
     * @return ignore
     */
    Token createToken(User user, String remoteAddress, String userAgent);

}
