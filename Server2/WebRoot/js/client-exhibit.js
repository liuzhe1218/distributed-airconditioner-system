Ext.require([
    'Ext.panel.*'
]);

Ext.namespace('Panel'),
Ext.onReady(function() {   
    // Normal panel
    Ext.create('widget.panel', {
        id: 'ui-panel',   
        renderTo: 'clientexhibitpanel',
        title: 'Plain Old Panel',
        width: 200,
        autoHeight: true,
        frame: true,
        
        contentEl: 'bubble-markup',
        
        html: 'TEST'
    });
});
