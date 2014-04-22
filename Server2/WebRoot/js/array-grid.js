Ext.require([
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.state.*'
]);

// Define client-monitor entity
// Null out built in convert functions for performance *because the raw data is known to be valid*
// Specifying defaultValue as undefined will also save code. *As long as there will always be values in the data, or the app tolerates undefined field values*
Ext.define('client-monitor', {
    extend: 'Ext.data.Model',
    fields: [
       {name: 'ClientID'},
       {name: 'IndoorT',      type: 'float', convert: null,     defaultValue: undefined},
       {name: 'Speed', type: 'date',  dateFormat: 'n/j h:ia', defaultValue: undefined}
    ],
    idProperty: 'client-monitor'
});

Ext.onReady(function() {
    Ext.QuickTips.init();
    
    // setup the state provider, all state information will be saved to a cookie
    Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));

    // sample static data for the store
    var myData = [
        ['1',25, '9/1 12:00am'],
        ['2',16, '9/1 12:00am'],
		['3',21, '9/1 12:00am']
    ];

    // create the data store
    var store = Ext.create('Ext.data.ArrayStore', {
        model: 'client-monitor',
        data: myData
    });

    // create the Grid
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        stateful: true,
        collapsible: true,
        multiSelect: true,
        stateId: 'stateGrid',
        columns: [
            {
                text     : 'ClientID',
                flex     : 1,
                sortable : false,
                dataIndex: 'ClientID'
            },
            {
                text     : 'Temprature',
                width    : 100,
                sortable : true,
                <!--renderer : 'usMoney',-->
                dataIndex: 'IndoorT'
            },
            {
                text     : 'Speed',
                width    : 100,
                sortable : true,
                renderer : Ext.util.Format.dateRenderer('m/d/Y'),
                dataIndex: 'Speed'
            },
        ],
        height: 150,
        width: 300,
        title: 'Vlient Working State Monitor',
        renderTo: 'grid-example',
        viewConfig: {
            stripeRows: true,
            enableTextSelection: true
        }
    });
});
