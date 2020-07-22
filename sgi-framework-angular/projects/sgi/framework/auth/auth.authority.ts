
/**
 * Checks if the user authorities contains an authority
 *
 * @param userAuthorities User authorities to scan
 * @param authority Authority to check
 */
export function hasAuthority(userAuthorities: string[], authority: string): boolean {
  return userAuthorities.find((auth) => auth === authority) ? true : false;
}

/**
 * Checks if the user authorities contains any of the provided authorities
 *
 * @param userAuthorities User authorities to scan
 * @param authorities Authorities to check
 */
export function hasAnyAuthority(userAuthorities: string[], authorities: string[]): boolean {
  return userAuthorities.find((auth) => authorities.find((au) => auth === au)) ? true : false;
}

/**
 * Checks if the user authorities contains an authority.
 * OU prefix in the user authorities is ignored, so the authority to find must not containt it.
 *
 * @param userAuthorities User authorities to scan
 * @param authority Authority to check
 */
export function hasAuthorityForAnyUO(userAuthorities: string[], authority: string): boolean {
  return userAuthorities.find((auth) => auth.match(new RegExp('^' + authority + '($|_.+$)'))) ? true : false;
}

/**
 * Checks if the user authorities contains any of the provided authorities.
 * OU prefix in the user authorities is ignored, so the authorities to find must not containt it.
 *
 * @param userAuthorities User authorities to scan
 * @param authorities Authorities to check
 */
export function hasAnyAuthorityForAnyUO(userAuthorities: string[], authorities: string[]): boolean {
  return userAuthorities.find((auth) => authorities.find((au) => auth.match(new RegExp('^' + au + '($|_.+$)')))) ? true : false;
}
