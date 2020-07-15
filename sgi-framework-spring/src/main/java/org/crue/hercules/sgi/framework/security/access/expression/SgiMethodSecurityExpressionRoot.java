package org.crue.hercules.sgi.framework.security.access.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class SgiMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {
  private Set<String> roles;
  private Map<String, List<String>> rolesMap;
  private RoleHierarchy roleHierarchy;
  private String defaultRolePrefix = "ROLE_";

  private Object filterObject;
  private Object returnObject;
  private Object target;

  public void setFilterObject(Object filterObject) {
    this.filterObject = filterObject;
  }

  public Object getFilterObject() {
    return filterObject;
  }

  public void setReturnObject(Object returnObject) {
    this.returnObject = returnObject;
  }

  public Object getReturnObject() {
    return returnObject;
  }

  /**
   * Sets the "this" property for use in expressions. Typically this will be the
   * "this" property of the {@code JoinPoint} representing the method invocation
   * which is being protected.
   *
   * @param target the target object on which the method in is being invoked.
   */
  void setThis(Object target) {
    this.target = target;
  }

  public Object getThis() {
    return target;
  }

  public SgiMethodSecurityExpressionRoot(Authentication authentication) {
    super(authentication);
  }

  public final boolean hasAuthorityForAnyUO(String authority) {
    return hasAnyAuthorityForAnyUO(authority);
  }

  public final boolean hasAnyAuthorityForAnyUO(String... authorities) {
    return hasAnyAuthorityNameForAnyUO(null, authorities);
  }

  public final boolean hasRoleForAnyUO(String role) {
    return hasAnyRoleForAnyUO(role);
  }

  public final boolean hasAnyRoleForAnyUO(String... roles) {
    return hasAnyAuthorityNameForAnyUO(defaultRolePrefix, roles);
  }

  public final String[] getAuthorityUOs(String authority) {
    return getAuthorityNameUOs(null, authority);
  }

  public final String[] getRolUOs(String authority) {
    return getAuthorityNameUOs(defaultRolePrefix, authority);
  }

  private boolean hasAnyAuthorityNameForAnyUO(String prefix, String... roles) {
    Set<String> roleSet = getAuthoritySet();

    for (String role : roles) {
      String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
      if (roleSet.contains(defaultedRole)) {
        return true;
      }
    }

    return false;
  }

  private String[] getAuthorityNameUOs(String prefix, String role) {
    Map<String, List<String>> roleMap = getAuthorityMap();
    String defaultedRole = getRoleWithDefaultPrefix(prefix, role);
    List<String> uoList = roleMap.get(defaultedRole);
    String[] uoArray = new String[] {};
    if (uoList != null) {
      return uoList.toArray(uoArray);
    }
    return uoArray;
  }

  /**
   * <p>
   * Sets the default prefix to be added to {@link #hasAnyRole(String...)} or
   * {@link #hasRole(String)}. For example, if hasRole("ADMIN") or
   * hasRole("ROLE_ADMIN") is passed in, then the role ROLE_ADMIN will be used
   * when the defaultRolePrefix is "ROLE_" (default).
   * </p>
   *
   * <p>
   * If null or empty, then no default role prefix is used.
   * </p>
   *
   * @param defaultRolePrefix the default prefix to add to roles. Default "ROLE_".
   */
  @Override
  public void setDefaultRolePrefix(String defaultRolePrefix) {
    super.setDefaultRolePrefix(defaultRolePrefix);
    this.defaultRolePrefix = defaultRolePrefix;
  }

  /**
   * Prefixes role with defaultRolePrefix if defaultRolePrefix is non-null and if
   * role does not already start with defaultRolePrefix.
   *
   * @param defaultRolePrefix
   * @param role
   * @return
   */
  private static String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
    if (role == null) {
      return role;
    }
    if (defaultRolePrefix == null || defaultRolePrefix.length() == 0) {
      return role;
    }
    if (role.startsWith(defaultRolePrefix)) {
      return role;
    }
    return defaultRolePrefix + role;
  }

  private Set<String> getAuthoritySet() {
    if (roles == null) {
      Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

      if (roleHierarchy != null) {
        userAuthorities = roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
      }

      roles = authorityListToSetWithoutUO(userAuthorities);
    }

    return roles;
  }

  private Map<String, List<String>> getAuthorityMap() {
    if (rolesMap == null) {
      Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();

      if (roleHierarchy != null) {
        userAuthorities = roleHierarchy.getReachableGrantedAuthorities(userAuthorities);
      }

      rolesMap = authorityListToMapWithUOs(userAuthorities);
    }

    return rolesMap;
  }

  /**
   * Converts an array of GrantedAuthority objects to a Set.
   * 
   * @return a Set of the Strings obtained from each call to
   *         GrantedAuthority.getAuthority()
   */
  private static Set<String> authorityListToSetWithoutUO(Collection<? extends GrantedAuthority> userAuthorities) {
    Assert.notNull(userAuthorities, "userAuthorities cannot be null");
    Set<String> set = new HashSet<>(userAuthorities.size());

    for (GrantedAuthority authority : userAuthorities) {
      String auth = authority.getAuthority();
      set.add(getAuthorityWithoutUO(auth));
    }

    return set;
  }

  /**
   * Converts an array of GrantedAuthority objects to a Map of Authorities and
   * Unidades Organizativas.
   * 
   * @return a Map of the Strings obtained from each call to
   *         GrantedAuthority.getAuthority()
   */
  private static Map<String, List<String>> authorityListToMapWithUOs(
      Collection<? extends GrantedAuthority> userAuthorities) {
    Assert.notNull(userAuthorities, "userAuthorities cannot be null");
    Map<String, List<String>> map = new HashMap<>();

    for (GrantedAuthority authority : userAuthorities) {
      String auth = authority.getAuthority();
      String uo = null;
      int index = auth.indexOf("_");
      if (index != -1) {
        uo = auth.substring(index + 1, auth.length());
        auth = auth.substring(0, index);
      }
      List<String> uoList = map.get(auth);
      if (uoList == null) {
        uoList = new ArrayList<>();
      }
      if (uo != null) {
        uoList.add(uo);
      }
      map.put(auth, uoList);
    }

    return map;
  }

  private static String getAuthorityWithoutUO(String authority) {
    int index = authority.indexOf("_");
    if (index != -1) {
      return authority.substring(0, index);
    }
    return authority;
  }

  @Override
  public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
    super.setRoleHierarchy(roleHierarchy);
    this.roleHierarchy = roleHierarchy;
  }

}