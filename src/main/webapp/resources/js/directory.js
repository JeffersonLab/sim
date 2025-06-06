var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Software';
jlab.editableRowTable.dialog.width = 650;
jlab.editableRowTable.dialog.height = 650;
jlab.addRow = function() {
    var name = $("#row-name").val(),
        type = $("#row-type").val(),
        topicArray = $("#row-topics").val(),
        description = $("#row-description").val(),
        note = $("#row-note").val(),
        maintainerUsernameCsv = $("#row-maintainers").val(),
        homeUrl = $("#row-url").val(),
        repositoryId = $("#row-repo").val(),
        archived = $("#row-archived").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/add-software",
        type: "POST",
        data: {
            repositoryId: repositoryId,
            name: name,
            type: type,
            topicArray: topicArray,
            description: description,
            note: note,
            maintainerUsernameCsv: maintainerUsernameCsv,
            homeUrl: homeUrl,
            archived: archived
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to add software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.editRow = function(removeSync) {
    var softwareId = $(".editable-row-table tr.selected-row").attr("data-id"),
        name = $("#row-name").val(),
        type = $("#row-type").val(),
        topicArray = $("#row-topics").val(),
        description = $("#row-description").val(),
        note = $("#row-note").val(),
        maintainerUsernameCsv = $("#row-maintainers").val(),
        homeUrl = $("#row-url").val(),
        repositoryId = $("#row-repo").val(),
        archived = $("#row-archived").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/edit-software",
        type: "POST",
        data: {
            softwareId: softwareId,
            repositoryId: repositoryId,
            name: name,
            type: type,
            topicArray: topicArray,
            description: description,
            note: note,
            maintainerUsernameCsv: maintainerUsernameCsv,
            homeUrl: homeUrl,
            archived: archived
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to edit software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");

        }
    });
};
jlab.removeRow = function() {
    var name = $(".editable-row-table tr.selected-row td:first-child").text(),
        id = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    $("#remove-row-button")
        .height($("#remove-row-button").height())
        .width($("#remove-row-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/remove-software",
        type: "POST",
        data: {
            softwareId: id
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to remove software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $("#remove-row-button").empty().text("Remove");
        }
    });
};
$(document).on("dialogclose", "#table-row-dialog", function() {
    $("#row-form")[0].reset();

    $("#row-topics").val(null).trigger("change");

    $("#row-name").removeAttr("disabled");
    $("#row-repo").removeAttr("disabled");
});
$(document).on("click", "#open-edit-row-dialog-button", function() {
    var $selectedRow = $(".editable-row-table tr.selected-row");
    $("#row-name").val($selectedRow.attr("data-name"));
    $("#row-type").val($selectedRow.attr("data-type"));
    $("#row-description").val($selectedRow.attr("data-description"));
    $("#row-note").val($selectedRow.attr("data-note"));
    $("#row-maintainers").val($selectedRow.attr("data-maintainer-csv"));
    $("#row-repo").val($selectedRow.attr("data-repo-id"));
    $("#row-topics").val($selectedRow.attr("data-topic-csv").split(",")).trigger('change');
    $("#row-url").val($selectedRow.attr("data-url"));
    $("#row-archived").val($selectedRow.attr("data-archived"));

    $("#row-name").attr("disabled", "disabled");
    $("#row-repo").attr("disabled", "disabled");
});
$(document).on("table-row-add", function() {
    jlab.addRow();
});
$(document).on("table-row-edit", function() {
    jlab.editRow();
});
$(document).on("click", "#remove-row-button", function() {
    var name = $(".editable-row-table tr.selected-row td:first-child").text().trim();
    if (confirm('Are you sure you want to remove ' + name + '?')) {
        jlab.removeRow();
    }
});
$(document).on("click", ".default-clear-panel", function () {
    $("#software-name").val('');
    $("#username").val('');
    $("#repository-select").val('');
    $("#topic-select").val(null).trigger('change');
    $("#type-select").val('');
    $("#archived-select").val('');
    return false;
});
$(document).on("change", ".change-submit", function () {
    let formId = $(this).attr("form");
    document.getElementById(formId).submit();
});
$(function(){
    $("#row-topics").select2({
        tags: true
    });

    $("#topic-select").select2();
});