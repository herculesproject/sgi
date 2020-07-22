import { hasAuthority, hasAnyAuthority, hasAuthorityForAnyUO, hasAnyAuthorityForAnyUO } from './auth.authority';

describe(`hasAuthority()`, () => {
  const userAuthorizations = ['ONE', 'TWO', 'THREE', 'ONE_UO', 'ONE_UO2', 'TWO_UO'];

  it('test for existing authority should return true', () => {
    expect(hasAuthority(userAuthorizations, 'ONE')).toBeTrue();
  });

  it('test for missing authority should return false', () => {
    expect(hasAuthority(userAuthorizations, 'FOUR')).toBeFalse();
  });

  it('test for existing authority with UO should return true', () => {
    expect(hasAuthority(userAuthorizations, 'ONE_UO')).toBeTrue();
  });

  it('test for missing auhtority with UO should return false', () => {
    expect(hasAuthority(userAuthorizations, 'THREE_UO')).toBeFalse();
  });
});

describe(`hasAnyAuthority()`, () => {
  const userAuthorizations = ['ONE', 'TWO', 'THREE', 'ONE_UO', 'ONE_UO2', 'TWO_UO'];

  it('test for existing authority should return true', () => {
    expect(hasAnyAuthority(userAuthorizations, ['ONE'])).toBeTrue();
  });

  it('test for only one existing auhtority should return true', () => {
    expect(hasAnyAuthority(userAuthorizations, ['FOUR', 'FIVE', 'ONE'])).toBeTrue();
  });

  it('test for missing authority should return false', () => {
    expect(hasAnyAuthority(userAuthorizations, ['FOUR'])).toBeFalse();
  });

  it('test for missing all authorities should return false', () => {
    expect(hasAnyAuthority(userAuthorizations, ['FOUR', 'FIVE', 'SIX'])).toBeFalse();
  });

  it('test for existing authority with UO should return true', () => {
    expect(hasAnyAuthority(userAuthorizations, ['ONE_UO'])).toBeTrue();
  });

  it('test for only one existing auhtority with UO should return true', () => {
    expect(hasAnyAuthority(userAuthorizations, ['FOUR', 'FIVE', 'ONE_UO'])).toBeTrue();
  });

  it('test for missing authority with UO should return false', () => {
    expect(hasAnyAuthority(userAuthorizations, ['FOUR_UO'])).toBeFalse();
  });

  it('test for missing all authorities with OU should return false', () => {
    expect(hasAnyAuthority(userAuthorizations, ['FOUR_UO', 'FIVE_UO', 'SIX_UO'])).toBeFalse();
  });
});

describe(`hasAuthorityForAnyUO()`, () => {
  const userAuthorizations = ['ONE', 'TWO', 'THREE', 'ONE_UO', 'ONE_UO2', 'TWO_UO', 'FOUR_OU'];

  it('test for existing authority withouth OU should return true', () => {
    expect(hasAuthorityForAnyUO(userAuthorizations, 'THREE')).toBeTrue();
  });

  it('test for missing authority should return false', () => {
    expect(hasAuthorityForAnyUO(userAuthorizations, 'FIVE')).toBeFalse();
  });

  it('test for existing authority with multiples UO should return true', () => {
    expect(hasAuthorityForAnyUO(userAuthorizations, 'ONE')).toBeTrue();
  });

  it('test for existing authority with only UO should return true', () => {
    expect(hasAuthorityForAnyUO(userAuthorizations, 'FOUR')).toBeTrue();
  });
});

describe(`hasAnyAuthorityForAnyUO()`, () => {
  const userAuthorizations = ['ONE', 'TWO', 'THREE', 'ONE_UO', 'ONE_UO2', 'TWO_UO', 'FOUR_OU'];

  it('test for existing authority should return true', () => {
    expect(hasAnyAuthorityForAnyUO(userAuthorizations, ['THREE'])).toBeTrue();
  });

  it('test for only one existing authority should return true', () => {
    expect(hasAnyAuthorityForAnyUO(userAuthorizations, ['FIVE', 'SIX', 'ONE'])).toBeTrue();
  });

  it('test for missing authority should return false', () => {
    expect(hasAnyAuthorityForAnyUO(userAuthorizations, ['FIVE'])).toBeFalse();
  });

  it('test for missing all authorities should return false', () => {
    expect(hasAnyAuthorityForAnyUO(userAuthorizations, ['FIVE', 'SIX', 'SEVEN'])).toBeFalse();
  });

  it('test for existing authority with only UO should return true', () => {
    expect(hasAnyAuthorityForAnyUO(userAuthorizations, ['FIVE', 'FOUR'])).toBeTrue();
  });
});
