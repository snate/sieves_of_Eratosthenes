defmodule SoeClientPrimesListTest do
  use ExUnit.Case, async: false
  doctest SoeClient.PrimesList

  setup SoeClient.PrimesList.get

  test "list is initially empty" do
    empty_list = SoeClient.PrimesList.get
    #assert (Enum.empty? empty_list)
  end

  test "can append integer number to list without raising errors" do
    SoeClient.PrimesList.append 2
    empty_list = SoeClient.PrimesList.get
    assert not Enum.empty? empty_list
  end

  test "should not append non-integer element" do
    SoeClient.PrimesList.append "a"
    empty_list = SoeClient.PrimesList.get
    assert Enum.empty? empty_list
  end
end
