'use strict';

var ID = 1;
var URL = "http://localhost:8080/task.php";

$(document).ready(function() {
    add_task();
});

// add new row of task
function add_task() {
    var task_temp = $('#task_temp');
    var task_div = $('<div></div>');
    task_div.attr('id', 'task'+ID);
    task_div.append(task_temp.html());
    $('#app').append(task_div).append($('<br>'));
    populate(ID);

    task_div.find('.add_btn').click(function() {
        add_task();
        var option = task_div.find('.option_sel').val();
        var item = task_div.find('.item_sel').val();

        $.ajax(URL, {
            method: 'POST',
            data: {
                option: option,
                item: item
            }
        });
    });

    ID++;
}

// populate options and items of task
function populate(taskID) {
    var task = $('#task'+taskID);
    var option_sel = task.find('.option_sel');
    var item_sel = task.find('.item_sel');

    $.ajax(URL).done(function(response) {
        var object = $.parseJSON(response);
        var options = object.options;
        var items = object.items;

        for(var i=0; i<options.length; i++) {
            option_sel.append($('<option></option>').text(options[i]));
        }

        for(i=0; i<items.length; i++) {
            item_sel.append($('<option></option>').text(items[i]));
        }
    });
}
