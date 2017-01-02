defmodule SoeClient.Computer do
  use GenServer

  @doc """
  Starts the primes.
  """
  def start_link do
    GenServer.start_link(__MODULE__, 0, name: :Computer)
  end

  def compute_up_to(n) do
    GenServer.call :Computer, {:compute, n}
  end

  # SERVER CALLBACKS
  def handle_call({:compute, n}, _from, 0) do
    how_many = get_primes_up_to 2, n, 0
    {:reply, :computing, how_many}
  end

  def handle_call({:compute, _n}, _from, counter) do
    {:reply, :busy, counter}
  end

  defp get_primes_up_to(n, limit, cnt)
  when (limit-n < 0 or not (is_integer(limit))) do
    cnt
  end

  defp get_primes_up_to(n, limit, cnt) do
    # register for answer
    GenServer.call endpoint, {:ask_for, {n, me}}
    IO.inspect "CALL for #{n}"
    # find out if n is prime
    GenServer.call backend, {:next_number, [0, n]}
    get_primes_up_to n+1, limit, cnt+1
  end

  def handle_cast({:answer, {num, :prime}}, 1) do
    [ num | SoeClient.PrimesList.get ]
    IO.inspect "FINISHED WITH PRIME #{num}"
    IO.inspect "COUNT WAS ONE\n\n"
    {:noreply, 0}
  end

  def handle_cast({:answer, {num, :prime}}, count) do
    SoeClient.PrimesList.append num
    IO.inspect "STILL DOING... RECEIVED PRIME #{num}"
    IO.inspect "COUNT IS #{count}\n\n"
    new_count = max 0, count-1
    {:noreply, new_count}
  end

  def handle_cast({:answer, {_num, :not_prime}}, 1) do
    SoeClient.PrimesList.get
    IO.inspect "FINISHED WITH NOT PRIME #{_num}"
    {:noreply, 0}
  end

  def handle_cast({:answer, {_num, :not_prime}}, count) do
    IO.inspect "STILL DOING... RECEIVED NOT PRIME #{_num}"
    new_count = max 0, count-1
    {:noreply, new_count}
  end

  defp me do
    {:Computer, node()}
  end

  defp backend do
    {:Receiver, Application.get_env(:soe_client, :backend_address)}
  end

  defp endpoint do
    {:Server, Application.get_env(:soe_client, :endpoint_address)}
  end

end
