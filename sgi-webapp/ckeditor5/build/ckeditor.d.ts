/**
 * @license Copyright (c) 2014-2023, CKSource Holding sp. z o.o. All rights reserved.
 * For licensing, see LICENSE.md or https://ckeditor.com/legal/ckeditor-oss-license
 */
import { ClassicEditor } from '@ckeditor/ckeditor5-editor-classic';
import { Alignment } from '@ckeditor/ckeditor5-alignment';
import { Bold, Italic, Underline } from '@ckeditor/ckeditor5-basic-styles';
import { Essentials } from '@ckeditor/ckeditor5-essentials';
import { FontBackgroundColor, FontColor, FontFamily, FontSize } from '@ckeditor/ckeditor5-font';
import { Heading } from '@ckeditor/ckeditor5-heading';
import { Link } from '@ckeditor/ckeditor5-link';
import { List } from '@ckeditor/ckeditor5-list';
import { Paragraph } from '@ckeditor/ckeditor5-paragraph';
import { Table, TableColumnResize, TableToolbar } from '@ckeditor/ckeditor5-table';
declare class Editor extends ClassicEditor {
  static builtinPlugins: (typeof Alignment | typeof Bold | typeof Essentials | typeof FontBackgroundColor | typeof FontColor | typeof FontFamily | typeof FontSize | typeof Heading | typeof Italic | typeof Link | typeof List | typeof Paragraph | typeof Table | typeof TableColumnResize | typeof TableToolbar | typeof Underline)[];
  static defaultConfig: {
    toolbar: {
      items: string[];
    };
    link: {};
    language: string;
    table: {
      contentToolbar: string[];
    };
  };
}
export default Editor;
