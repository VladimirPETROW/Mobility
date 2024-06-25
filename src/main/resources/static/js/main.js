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

    var form = $(".form-signin");
    if (form) {
        form.on("submit", function(event) {
            $.each($.cookie(), function(name) {
                $.removeCookie(name);
            });
        });
    }
});

