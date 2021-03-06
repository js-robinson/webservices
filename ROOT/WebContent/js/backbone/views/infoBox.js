//js/views/infoBox.js

var app = app || {};

/**
 * InfoBox
 * ---------------------------------
 * the UI for 'infoBox'
 */

app.InfoBoxView = Backbone.View.extend({
  el: '#info_box',

  events: {
    'click .input-group-btn': 'createTopic',
    'click .btn-submit': 'submitTopic',
    'click .btn-refresh': 'refreshTopics',
    'click .btn-clear': 'clearTopic'
  },

  refreshTopics: function() {
    this.clearTopic();
    this.initialize();
  },

  initialize: function() {
    this.$el.attr("style", "height: " + (chart.svg.height - 60) * 0.66 + "px");

    // Brings a list of topics from FEWS services
    app.Topics.fetch({
      // headers: {'Authorization': localStorage.getItem('auth_token')},
      success: function(data) {
        if (data) {
          data.forEach(function(d) {
            app.infoBoxView.addTopic(d.attributes);
          });
        }
      },
      error: function(response) {
        console.error("An error occurred fetching topics from the Fact Extraction service. Please contact the system administrator.");
        alert("An error occurred fetching topics from the Fact Extraction service. Please contact the system administrator.");
        console.error(response);
      }
    });

    // Brings tweets related to listed topics periodically(30s)
    // var timer = setInterval( "app.infoBoxView.submitTopic()", 30000 );

    this.$("input").on("keydown", function(event) {
      if (event.which == 13 || event.keyCode == 13) {
        app.infoBoxView.createTopic();
      }
    });
  },

  render: function() {},

  createTopic: function() {
    var topic = this.$(".input-group input").val();

    if (!topic || typeof(topic) == "undefined" || topic == "") {
      alert("Please, enter a topic");
      return;
    }

    var param = {
      'name': topic,
      'negated': -1,
      'genuine': -1
    };

    this.addTopic(param);

    this.$(".input-group input").val("");
  },

  addTopic: function(topic) {

    // If the same topic exists in the list, the name of radio button should be changed
    var radio_btn_name = topic.name;
    while ($('[name="negated-' + radio_btn_name + '"]').length > 0) {
      radio_btn_name = radio_btn_name + "_1";
    }

    var p = $("<p></p>").appendTo(this.$(".topic-form"));

    var div = $("<div></div>", {
      "class": "form-group"
    }).appendTo(p);

    var alert_class = "alert-success";
    if (topic.negated == 0) {
      alert_class = "alert-danger";
    } else if (topic.negated == -1) {
      alert_class = "alert-warning";
    }

    var div_alert = $("<div></div>", {
      "class": "alert " + alert_class + " alert-infobox float_left",
      "name": radio_btn_name,
      "text": topic.name
    }).appendTo(div);

    var button = $("<i></i>", {
      "class": "fa fa-minus"
    }).appendTo($("<button></button>", {
      "type": "button",
      "class": "btn btn-default"
    }).click(function() {
      p.remove();
    }).appendTo(div));

    var div_negated = $("<div></div>", {
      "class": "form-group"
    }).appendTo(p);

    var negated_true = $("<input/>", {
      "type": "radio",
      "name": "negated-" + radio_btn_name,
      "value": 1,
      "checked": (topic.negated == 1)
    }).click(function(obj) {
      div_alert.addClass("alert-success");
      div_alert.removeClass("alert-danger");
      div_alert.removeClass("alert-warning");
    }).appendTo(
      $("<label></label>", {
        "class": "radio-inline"
      }).appendTo(div_negated)
      .before($("<label></label>", {
        "text": "negated"
      }))
    ).after($("<span></span>", {
      "text": "true"
    }));

    var negated_false = $("<input/>", {
      "type": "radio",
      "name": "negated-" + radio_btn_name,
      "value": 0,
      "checked": (topic.negated == 0)
    }).click(function() {
      div_alert.removeClass("alert-success");
      div_alert.addClass("alert-danger");
      div_alert.removeClass("alert-warning");
    }).appendTo(
      $("<label></label>", {
        "class": "radio-inline"
      }).appendTo(div_negated)
    ).after($("<span></span>", {
      "text": "false"
    }));

    var negated_unknown = $("<input/>", {
      "type": "radio",
      "name": "negated-" + radio_btn_name,
      "value": -1,
      "checked": (topic.negated == -1)
    }).click(function() {
      div_alert.removeClass("alert-success");
      div_alert.removeClass("alert-danger");
      div_alert.addClass("alert-warning");
    }).appendTo(
      $("<label></label>", {
        "class": "radio-inline"
      }).appendTo(div_negated)
    ).after($("<span></span>", {
      "text": "unknown"
    }));

    var div_genuine = $("<div></div>", {
      "class": "form-group"
    }).appendTo(p);

    var genuine_true = $("<input/>", {
      "type": "radio",
      "name": "genuine-" + radio_btn_name,
      "value": 1,
      "checked": (topic.genuine == 1)
    }).appendTo(
      $("<label></label>", {
        "class": "radio-inline"
      }).appendTo(div_genuine)
      .before($("<label></label>", {
        "text": "genuine"
      }))
    ).after($("<span></span>", {
      "text": "true"
    }));

    var genuine_false = $("<input/>", {
      "type": "radio",
      "name": "genuine-" + radio_btn_name,
      "value": 0,
      "checked": (topic.genuine == 0)
    }).appendTo(
      $("<label></label>", {
        "class": "radio-inline"
      }).appendTo(div_genuine)
    ).after($("<span></span>", {
      "text": "false"
    }));

    var genuine_unknown = $("<input/>", {
      "type": "radio",
      "name": "genuine-" + radio_btn_name,
      "value": -1,
      "checked": (topic.genuine == -1)
    }).appendTo(
      $("<label></label>", {
        "class": "radio-inline"
      }).appendTo(div_genuine)
    ).after($("<span></span>", {
      "text": "unknown"
    }));
  },

  submitTopic: function() {
    var topic_list = [];

    var p = this.$(".topic-form p");
    for (var i = 0; i < p.length; i++) {
      var topic = p[i].childNodes[0].childNodes[0].getAttribute("name");

      var obj = {
        "name": p[i].childNodes[0].childNodes[0].innerText,
        "negated": $('[name="negated-' + topic + '"]:checked').val(),
        "genuine": $('[name="genuine-' + topic + '"]:checked').val()
      };

      topic_list.push(obj);
    }

    Backbone.ajax({
      type: "POST",
      url: remote_server + "/fewsservlet/tweets",
      data: JSON.stringify(topic_list),
      dataType: 'json',
      contentType: "application/json",
      success: function(result) {
        $(".fews-form table tbody").empty();

        if (result) {
          result.forEach(function(data) {
            var tr = $("<tr></tr>", {
                "class": "extract_tr",
                "draggable": true
              }).appendTo($(".fews-form table tbody"))
              .on("dragstart", function(obj) {
                obj.originalEvent.dataTransfer.setData('text/plain', null);
              })
              .on("dragend", function(obj) {
                if(view_flag){
                  return null;
                }

                var children = this.childNodes;

                // creates model of the node
                var attr = app.workBoxView.createNode("info", children[1].innerText, children[0].innerText);

                var restart = true;
                if (!chart.nodes || chart.nodes.length < 1)
                  restart = false;

                // draws a new node
                chart.node = addNewNode(attr, obj.pageX, obj.pageY);

                // re-start changed graph
                chart.simulation = restart_simulation(chart.simulation, restart);
              });

            var td_extract = $("<td></td>", {
                "text": data.extract
              }).appendTo(tr)
              .click(function() {
                $("#details-node").hide();
                $("#details-tweet").show();

                var p = $("#details-tweet p");
                p[0].innerText = data.extract;
                p[1].innerText = data.text;
                p[2].innerText = data.uri;
                p[3].innerText = new Date(data.created.substring(0, data.created.length - 6));
              });

            var td_uri = $("<td></td>", {
                "text": data.uri
              }).appendTo(tr)
              .dblclick(function() {
                var tweet_popup = window.open(data.uri, parseText(data.extract), "height: 100px,width: 150px");
              });
          });
        }
      },
      error: function(xhr, textStatus, errorThrown) {
        alert("AJAX failed: " + errorThrown);
      }
    });
  },

  clearTopic: function() {
    this.$(".topic-form").html(" ");
    this.$(".fews-form table tbody").empty();
  }
});
