Feature: Placing customer orders for delivery
  Items sold in an online store needs to be placed on our ERP for delivery. Once an order is accepted by the ERP, its status can be tracked online

  Scenario: Placing an order and waiting for the tracking information to be available
    A tracking endpoint will be created when an order is placed for delivery

    When the online store 00-396-261 sells a set of items at 2018-11-03T14:48:17.000000242 with order reference 7158
    Then an order identifier is generated for the order
    And the order status is make available to the online store in a new endpoint generated from the order identifier
