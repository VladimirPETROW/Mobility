$(document).ready(function () {
    $("#filter").on("keyup", function () {
        var txt = $.trim($(this).val());
        var regex = new RegExp(txt, "i");
        $(".searchable tr").hide();
        $(".searchable tr").filter(function () {
            return regex.test($(this).text());
        }).show();
        $.cookie($("#filter").attr("cookie"), txt);
    });
    var txt = $.cookie($("#filter").attr("cookie"));
    if (txt) {
        $("#filter").val(txt);
    }
    $("#filter").trigger("keyup");
    $(".filter-hidden").removeClass("filter-hidden");

    $(".form-signin").on("submit", function(event) {
        $.each($.cookie(), function(name) {
            $.removeCookie(name);
        });
    });

    $(".form-redirect").append("<input type='hidden' name='redirect' value='" + window.location.href + "'>");

    $(".demand").on('click', function() {
        window.location.href = "/demid?id=" + $(this).attr("id");
    });

    $(".validate").on("keyup", function() {
        var regex = new RegExp("^" + $(this).attr("validregex") + "$", "i");
        if (regex.test($(this).val())) {
            $(this).removeClass("invalid");
        }
        else {
            $(this).addClass("invalid");
        }
    });

    $("#demInfo #edit").on("click", function() {
        $("#demInfo").hide();
        $("#demInfo *[dem]").each(function() {
            var dem = "#demEdit #" + $(this).attr("dem");
            var demVal = $(this).attr("demVal");
            if (demVal) {
                $(dem).val(demVal);
            }
            else {
                $(dem).val($(this).text());
            }
            $(dem).trigger("keyup");
        });
        $("#demEdit").removeClass("d-none");
    });

    $("#demEdit #cancel").on("click", function(event) {
        event.preventDefault();
        $("#demEdit").addClass("d-none");
        $("#demInfo").show();
    });

    $("#demEdit").on("submit", function(event) {
        $("#demEdit .validate").each(function() {
            $(this).trigger("keyup");
        });
        $("#demEdit .validate").each(function() {
            if ($(this).hasClass("invalid")) {
                event.preventDefault();
                return false;
            }
        });
    });

});

