Feature: Placing customer orders for delivery
  Items sold in an online store needs to be placed on our ERP for delivery. Once an order is accepted by the ERP, its status can be tracked online

  Scenario: Placing an order and waiting for the tracking information to be available
    A tracking endpoint will be created when an order is placed for delivery

    When the online store 00-396-261 sells a set of items at 2018-11-03T14:48:17.000000242 with order reference 7158
    Then an order identifier is generated for the order
    And the order status is make available to the online store in a new endpoint generated from the order identifier

#Feature: Testing a REST API
#  Users should be able to submit GET and POST requests to a web service, represented by WireMock
#
#  Scenario: Data Upload to a web service
#    When users upload data on a project
#    Then the server should handle it and return a success status
#
#  Scenario: Data retrieval from a web service
#    When users want to get information on the Cucumber project
#    Then the requested data is returned