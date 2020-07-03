/**
 * Find options for a list of elements. Can be combined
 */
export interface FindOptions {
  /**
   * To request a page
   */
  page?: PageRequest;
  /**
   * To request a sort
   */
  sort?: Sort;
  /**
   * To request a group of filters
   */
  filters?: Filter[];
}

/**
 * Page to request
 */
export interface PageRequest {
  /**
   * The number of elements per page
   */
  size: number;
  /**
   * The page index
   */
  index: number;
}

/**
 * Sort to apply
 */
export interface Sort {
  /**
   * The field to sort on
   */
  field: string;
  /**
   * The direction of the sort
   */
  direction: Direction;
}

/**
 * Sort direction
 */
export class Direction {
  /** Ascending */
  public static readonly ASC = new Direction('+');
  /** Descending */
  public static readonly DESC = new Direction('-');

  private constructor(
    private value: string
  ) { }

  /**
   * Maps values of MatSorte direction.
   * @param sortDirection MatSorter direction
   */
  public static fromSortDirection(sortDirection: '' | 'asc' | 'desc'): Direction {
    if (sortDirection === 'asc') {
      return Direction.ASC;
    }
    if (sortDirection === 'desc') {
      return Direction.DESC;
    }
    return undefined;
  }

  public toString(): string {
    return this.value;
  }
}

/**
 * Filter to apply
 */
export interface Filter {
  /**
   * The entity field name
   */
  field: string;
  /**
   * The type of the filter
   */
  type: FilterType;
  /**
   * The value to apply
   */
  value: string;
}


/**
 * Filter type
 */
export class FilterType {

  private constructor(
    private value: string
  ) { }

  /** No filter */
  public static readonly NONE = new FilterType(undefined);
  /** Filter by 'equals' operator */
  public static readonly EQUALS = new FilterType(':');
  /** Filter by 'notEquals' operator */
  public static readonly NOT_EQUALS = new FilterType('!:');
  /** Filter by 'like' operator */
  public static readonly LIKE = new FilterType('~');
  /** Filter by 'notLike' operator */
  public static readonly NOT_LIKE = new FilterType('!~');
  /** Filter by 'greather' operator */
  public static readonly GREATHER = new FilterType('>');
  /** Filter by 'greatherOrEqual' operator */
  public static readonly GREATHER_OR_EQUAL = new FilterType('>:');
  /** Filter by 'lower' operator */
  public static readonly LOWER = new FilterType('<');
  /** Filter by 'lowerOrEqual' operator */
  public static readonly LOWER_OR_EQUAL = new FilterType('<:');

  private reference = FilterType;

  public toString(): string {
    return this.value;
  }
}

/**
 * Result of request for a list of elements
 */
export interface ListResult<T> {
  /**
   * Page information
   */
  page: Page;
  /**
   * Total number of elements
   */
  total: number;
  /**
   * Returned elements
   */
  items: T[];
}

/**
 * Page information
 */
interface Page {
  /**
   * The number of elements per page
   */
  size: number;
  /**
   * The page index
   */
  index: number;
  /**
   * Elements in the page
   */
  count: number;
  /**
   * Total number of pages
   */
  total: number;
}
