var jlab = jlab || {};
jlab.addRow = function($tr, batch) {
    var name = $tr.attr("data-name"),
        type = $tr.attr("data-type"),
        topicCsv = $tr.attr("data-topic-csv"),
        topicArray = topicCsv.split(','),
        description = $tr.attr("data-description"),
        maintainerUsernameCsv = $tr.attr("data-maintainer"),
        homeUrl = $tr.attr("data-url"),
        repositoryId = $("#sync-table").attr("data-repo-id"),
        $button = $tr.find("button.add");

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/add-software",
        type: "POST",
        data: {
            repositoryId: repositoryId,
            name: name,
            type: type,
            topicArray: topicArray,
            description: description,
            maintainerUsernameCsv: maintainerUsernameCsv,
            homeUrl: homeUrl,
            archived: 'N'
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            $button.replaceWith("Success!");

            if(batch) {
                var $tr = jlab.addTr.pop();
                if($tr !== undefined) {
                    jlab.addRow($tr, true);
                } else {
                    if(jlab.removeTr.length === 0 && jlab.updateTr.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to add software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Add");
    });
};
jlab.removeRow = function($tr, batch) {
    var id = $tr.attr("data-id"),
        $button = $tr.find("button");

    $button
        .height($button.height())
        .width($button.width())
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
            $button.replaceWith("Success!");

            if(batch) {
                var $tr = jlab.removeTr.pop();
                if($tr !== undefined) {
                    jlab.removeRow($tr, true);
                } else {
                    if(jlab.addTr.length === 0 && jlab.updateTr.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to remove alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Remove");
    });
};
jlab.updateRow = function($tr, batch) {
    var softwareId = $tr.attr("data-id"),
        type = $tr.attr("data-type"),
        description = $tr.attr("data-description"),
        maintainerUsernameCsv = $tr.attr("data-maintainer"),
        homeUrl = $tr.attr("data-url"),
        repositoryId = $("#sync-table").attr("data-repo-id"),
        $button = $tr.find("button.update");

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/edit-software",
        type: "POST",
        data: {
            softwareId: softwareId,
            repositoryId: repositoryId,
            type: type,
            description: description,
            maintainerUsernameCsv: maintainerUsernameCsv,
            homeUrl: homeUrl,
            archived: 'N'
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            $button.replaceWith("Success!");

            if(batch) {
                var $tr = jlab.updateTr.pop();
                if($tr !== undefined) {
                    jlab.updateRow($tr, true);
                } else {
                    if(jlab.addTr.length === 0 && jlab.removeTr.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to update software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Update");
    });
};
$(document).on("click", "#apply-all-button", function() {
    var $table = $(this).closest("table"),
        $updateButtons = $table.find("button.update"),
        $removeButtons = $table.find("button.remove"),
        $addButtons = $table.find("button.add");

    if($updateButtons.length > 0 || $removeButtons.length > 0 || $addButtons.length > 0) {
        var questionArray = [];

        if($updateButtons.length > 0) {
            questionArray.push('Update ' + $updateButtons.length);
        }

        if($removeButtons.length > 0) {
            questionArray.push('Remove ' + $removeButtons.length);
        }

        if($addButtons.length > 0) {
            questionArray.push('Add ' + $addButtons.length);
        }

        var question = 'Are you sure you want to ' + questionArray.join(', ');

        if(confirm(question)) {

            var $button = $("#apply-all-button");

            $button
                .height($button.height())
                .width($button.width())
                .empty().append('<div class="button-indicator"></div>');

            $button.attr("disabled", "disabled");

            jlab.updateTr = [];
            $updateButtons.each(function() {
                jlab.updateTr.push($(this).closest("tr"));
            });
            jlab.removeTr = [];
            $removeButtons.each(function() {
                jlab.removeTr.push($(this).closest("tr"));
            });
            jlab.addTr = [];
            $addButtons.each(function() {
                jlab.addTr.push($(this).closest("tr"));
            });

            if(jlab.updateTr.length > 0) {
                jlab.updateTr.reverse();

                jlab.updateRow(jlab.updateTr.pop(), true);
            }

            if(jlab.removeTr.length > 0) {
                jlab.removeTr.reverse();

                jlab.removeRow(jlab.removeTr.pop(), true);
            }

            if(jlab.addTr.length > 0) {
                jlab.addTr.reverse();

                jlab.addRow(jlab.addTr.pop(), true);
            }
        }
    }
});
$(document).on("click", "button.add", function() {
    jlab.addRow($(this).closest("tr"));
});
$(document).on("click", "button.remove", function() {
    jlab.removeRow($(this).closest("tr"));
});
$(document).on("click", "button.update", function() {
    jlab.updateRow($(this).closest("tr"));
});