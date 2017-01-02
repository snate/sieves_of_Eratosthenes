defmodule Soe.Receiver.Stash do
  use GenServer

  @doc """
  Starts the receiver's stash.
  """
  def start_link do
    GenServer.start_link(__MODULE__, -1, name: __MODULE__)
  end

  @doc """
  Get the value in the stash.
  """
  def get do
    GenServer.call(__MODULE__,{:get, []})
  end

  @doc """
  Update the value in the stash.
  """
  def update(num) do
    GenServer.call(__MODULE__,{:update, [num]})
  end

  # SERVER CALLBACKS
  def handle_call({:get, []}, _from, num) do
    {:reply, num, num}
  end

  def handle_call({:update, [new_num]}, _from, _num) do
    {:reply, new_num, new_num}
  end
end
