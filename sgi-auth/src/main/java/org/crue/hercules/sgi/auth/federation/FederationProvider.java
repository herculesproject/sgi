/**
 *
 */
package org.crue.hercules.sgi.auth.federation;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.storage.user.ImportedUserValidation;
import org.keycloak.storage.user.UserLookupProvider;

/**
 * @author Treelogic
 */
@Stateful
@Local(FederationProvider.class)
public class FederationProvider implements UserStorageProvider, UserLookupProvider, ImportedUserValidation {

  private static final Logger LOGGER = Logger.getLogger(FederationProvider.class);

  protected ComponentModel model;
  protected KeycloakSession session;
  protected FederationProviderFactory factory;
  @PersistenceContext(unitName = "sgi")
  protected EntityManager em;

  public FederationProvider() {

  }

  public FederationProvider(KeycloakSession session, UserStorageProviderModel model, FederationProviderFactory factory,
      EntityManager em) {
    this.session = session;
    this.model = model;
    this.em = em;
    this.factory = factory;
  }

  public void setModel(ComponentModel model) {
    this.model = model;
  }

  public void setSession(KeycloakSession session) {
    this.session = session;
  }

  @Remove
  @Override
  public void close() {

  }

  @Override
  public UserModel validate(RealmModel realm, UserModel user) {
    // TODO: Revisar
    return user;
  }

  @Override
  public UserModel getUserById(String id, RealmModel realm) {
    return session.userLocalStorage().getUserById(id, realm);
  }

  @Override
  public UserModel getUserByUsername(String username, RealmModel realm) {
    try {
      UserModel m = session.userLocalStorage().getUserByUsername(username, realm);
      if (m == null) {
        m = session.userLocalStorage().addUser(realm, username);
      }
      m.setEnabled(true);
      return m;
    } catch (NoResultException e) {
      return null;
    }

  }

  @Override
  public UserModel getUserByEmail(String email, RealmModel realm) {
    return session.userLocalStorage().getUserByEmail(email, realm);
  }

}
