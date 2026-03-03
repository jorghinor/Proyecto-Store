import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/themes/store/views/dashboard-view.css?inline';

injectGlobalCss($cssFromFile_0.toString(), 'CSSImport end', document);
import $cssFromFile_1 from 'Frontend/themes/store/layouts/main-layout.css?inline';

injectGlobalCss($cssFromFile_1.toString(), 'CSSImport end', document);
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/combo-box/theme/lumo/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/context-menu/theme/lumo/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-item.js';
import '@vaadin/multi-select-combo-box/theme/lumo/vaadin-multi-select-combo-box.js';
import '@vaadin/grid/theme/lumo/vaadin-grid.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-sorter.js';
import '@vaadin/checkbox/theme/lumo/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/buttonFunctions.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-layout.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/app-layout/theme/lumo/vaadin-drawer-toggle.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/confirm-dialog/theme/lumo/vaadin-confirm-dialog.js';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import '@vaadin/login/theme/lumo/vaadin-login-form.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '8939eeced6aee38ae79c913d5fcd0cc38777f806032658248ea84eddb3c5baeb') {
    pending.push(import('./chunks/chunk-7828e70e23029f56390176aa5bc9d8dfecbdf57ccdc5f73b9e30da4692a91a39.js'));
  }
  if (key === 'd5e96240f80f7ab36f91ff9f78e3b6c1004923bc0572b108ca9c3808b7283749') {
    pending.push(import('./chunks/chunk-11126ff5dec72512d634fe45d4e433fc51aa09941f9213ef3c5a9cb1a0ef7dae.js'));
  }
  if (key === '6c230ac5162ff8107a63f6b7824181d35954338619957d716429937e84074b6e') {
    pending.push(import('./chunks/chunk-1b2e6cdc760aa26ab73772e6a2250c98aafb5ffa296e30906c7e6b42613eb96b.js'));
  }
  if (key === '6c375d310eb625c1fd86eec5e6cc23028800af04eee887aa6ca9a19de2845cc7') {
    pending.push(import('./chunks/chunk-8783e1a32e496fde555ca82b8943ee047b64d67317fb04521c44793827a8ef60.js'));
  }
  if (key === '0cb4fa621dadf1a028b3fa46592522fdc95854b5c6575a3f536dc002ea003b7a') {
    pending.push(import('./chunks/chunk-7828e70e23029f56390176aa5bc9d8dfecbdf57ccdc5f73b9e30da4692a91a39.js'));
  }
  if (key === '790bd7bf02375393a17d4a788ae66c310935ad3acaf7d3c57fd61280cef53c11') {
    pending.push(import('./chunks/chunk-b787403cb409566d3f27576eee3bc762243dc3ff680ea84ff6bbe14463b2c908.js'));
  }
  if (key === '058cf11f33389d4fd164f03bb04e1142bb94b82c95488d5396f0ea6c8ce093eb') {
    pending.push(import('./chunks/chunk-be21582711fb37c12bf46e4ab7541c3ddd0659617b266e1bfd1558f7532e0b17.js'));
  }
  if (key === '0f5d3e508b9fb2f74567c7719f5c5ca67edec1326d129b979c47c7299b57f0bd') {
    pending.push(import('./chunks/chunk-11126ff5dec72512d634fe45d4e433fc51aa09941f9213ef3c5a9cb1a0ef7dae.js'));
  }
  if (key === '4a2e7d2c115d85258d58148fa136154e5e3102c1fa220d1c536677d135d2bc8d') {
    pending.push(import('./chunks/chunk-9f25d61166498825112cc9ecc3332c24b2c5a54f02cb067c3d9de832fa89a3b4.js'));
  }
  if (key === 'f11b74357de089a8fcb20f4b118ea1cea6f31bcd1e971aac7e5c231129c2a785') {
    pending.push(import('./chunks/chunk-9f25d61166498825112cc9ecc3332c24b2c5a54f02cb067c3d9de832fa89a3b4.js'));
  }
  if (key === 'a7d1d384d136bf4042900473a692f7fec0a3b1aef8a4fd08c4e40ca55c59b4d5') {
    pending.push(import('./chunks/chunk-9f25d61166498825112cc9ecc3332c24b2c5a54f02cb067c3d9de832fa89a3b4.js'));
  }
  if (key === 'bba371d4c7afafb41bf3d854c127615dfb7eecbcde6e211ceb56674bf281f033') {
    pending.push(import('./chunks/chunk-9d14b880349188d19dcf2224a90002504ded057219cce1f5f3c582b7f8330912.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}