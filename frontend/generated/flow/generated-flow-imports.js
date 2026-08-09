import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '18e8c6a2e2b76cb24ebe67ad6c023bb449031be6bbe7bf0ffc15160b0cdf241d') {
    pending.push(import('./chunks/chunk-0d666bff9cbedb08586d682fc392d5d69d50f5b00d858bc55233becbc7efe354.js'));
  }
  if (key === '968c787fbb7c56e0336a5408afc6c93dd2ee9a267f7cec61b4c295ccf372682a') {
    pending.push(import('./chunks/chunk-0d666bff9cbedb08586d682fc392d5d69d50f5b00d858bc55233becbc7efe354.js'));
  }
  if (key === 'de0ea04fbada253909dcd8e00e2d76a8d7bef210c1f450178b26abacaa8902a6') {
    pending.push(import('./chunks/chunk-51ad01657d46b6c56ecaafadb0c7d64619eff019df393680db012de575c1821a.js'));
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