defmodule Soe.Receiver do
  use GenServer

  @doc """
  Starts the next number's receiver.
  """
  def start_link do
    {:ok, pid} = Soe.Receiver.Stash.start_link
    GenServer.start_link(__MODULE__, pid, name: __MODULE__)
  end

  @doc """
  Receive a request to analyze number `num from sieve with id `id.
  """
  def next_number(id, num) do
    GenServer.call(__MODULE__,{:next_number, [id, num]})
  end

  # SERVER CALLBACKS
  def handle_call({:next_number, [id, num]}, _from, stash)
    when (not is_integer(id)) or (not is_integer(num)) do
    {:reply, :bad_argument, stash}
  end

  def handle_call({:next_number, [id, num]}, _from, stash) do
    # create new sieve
    max_id = Soe.Receiver.Stash.get
    IO.puts max_id
    process_next_number(id, num, max_id)
    {:reply, :done, stash}
  end

  defp process_next_number(id, num, max_id)
    when id > max_id do
    # create new sieve
    Soe.SieveCreator.create_sieve id+1, num
    Soe.Receiver.Stash.update (id+1)
  end

  defp process_next_number(id, num, _max_id) do
    # forward to next sieve
    next_sieve = String.to_atom("Sieve" <> Integer.to_string(id + 1))
    GenServer.cast(next_sieve, {:is_prime?, num})
  end
end
