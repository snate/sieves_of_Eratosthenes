defmodule SoeClient.Computer do
  use GenServer

  @doc """
  Starts the primes.
  """
  def start_link do
    GenServer.start_link(__MODULE__, 0, name: :Computer)
  end


  @doc """
  Compute prime numbers up to `n.
  """
  def compute_up_to(n) do
    GenServer.call :Computer, {:compute, n}, 400000
  end

  # SERVER CALLBACKS
  def handle_call({:compute, n}, _from, 0) do
    SoeClient.Timer.set :os.system_time(:microsecond)
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
    GenServer.call endpoint(), {:ask_for, {n, me()}}
    # find out if n is prime
    GenServer.call backend(), {:next_number, {0, n}}
    get_primes_up_to n+1, limit, cnt+1
  end

  def handle_cast({:answer, {num, :prime}}, 1) do
    SoeClient.Timer.lap :os.system_time(:microsecond)
    [ num | SoeClient.PrimesList.get ]
    |> Enum.sort(&(&1 <= &2))
    |> IO.inspect
    {:noreply, 0}
  end

  def handle_cast({:answer, {num, :prime}}, count) do
    SoeClient.PrimesList.append num
    new_count = max 0, count-1
    {:noreply, new_count}
  end

  def handle_cast({:answer, {_num, :not_prime}}, 1) do
    SoeClient.Timer.lap :os.system_time(:microsecond)
    SoeClient.PrimesList.get
    |> Enum.sort(&(&1 <= &2))
    |> IO.inspect
    {:noreply, 0}
  end

  def handle_cast({:answer, {_num, :not_prime}}, count) do
    new_count = max 0, count-1
    {:noreply, new_count}
  end

  defp me do
    {:Computer, node()}
  end

  # backend and endpoint are not hard-coded but set up in the config files
  # thus it is easier to write tests for Computer using mock modules
  # that act like backend and endpoint
  defp backend do
    Application.get_env :soe_client, :backend_address
  end

  defp endpoint do
    Application.get_env :soe_client, :endpoint_address
  end

end
